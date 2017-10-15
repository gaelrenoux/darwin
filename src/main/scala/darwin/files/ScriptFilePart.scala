package darwin.files

import darwin.EvolutionPart.VariableToValues
import darwin._
import darwin.model.{Sql, Value, Variable}
import darwin.util.IncrementalTupleIterator
import play.api.libs.Collections

/**
  * Created by gael on 15/10/17.
  */
abstract class ScriptPart {
  val content: Map[Variable, Value] => Sql
  val using: Set[Variable]

  def apply(mapping: Map[Variable, Value]): Sql = content(mapping)

  def toEvolutionPart: EvolutionPart

  /** Function generating the list of scripts from a map from variables to a list of values */
  protected lazy val function: Function[VariableToValues, Seq[Sql]] = { variableToValues =>

    val variablesCount = using.size
    val orderedVariables = using.toSeq
    val orderedVariableValues = orderedVariables.map(variableToValues(_))
    val orderedVariableValuesCount = orderedVariableValues.map(_.size)

    /* If at least one of the variables has no value, then the script cannot be executed */
    if (orderedVariableValuesCount.contains(0)) Seq.empty
    else Collections.unfoldLeft(IncrementalTupleIterator(orderedVariableValuesCount)) { positionIt =>
      positionIt.next map { newPosition =>
        val orderedValues = Seq.tabulate(variablesCount) { i =>
          val variableValueChoice = positionIt(i)
          orderedVariableValues(i)(variableValueChoice)
        }
        val mapping = orderedVariables.zip(orderedValues).toMap
        (newPosition, this (mapping))
      }
    }
  }

}

case class ScriptUp(content: Map[Variable, Value] => Sql, using: Set[Variable]) extends ScriptPart {
  def toEvolutionPart = EvolutionUp(function)
}

case class ScriptDown(content: Map[Variable, Value] => Sql, using: Set[Variable]) extends ScriptPart {
  def toEvolutionPart = EvolutionDown(function)
}

case class ScriptDefine(variable: Variable, content: Map[Variable, Value] => Sql, using: Set[Variable]) extends ScriptPart {
  def toEvolutionPart = EvolutionDefine(variable, function)
}
