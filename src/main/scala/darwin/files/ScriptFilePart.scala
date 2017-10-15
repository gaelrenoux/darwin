package darwin.files

import darwin._
import darwin.model.{Sql, Value, Variable}
import darwin.util.IncrementalTupleIterator
import play.api.libs.Collections

/**
  * Created by gael on 15/10/17.
  */
abstract class ScriptFilePart {
  val content: Map[Variable, Value] => Sql
  val using: Set[Variable]

  def apply(mapping: Map[Variable, Value]): Sql = content(mapping)

  def toEvolutionPart: EvolutionPart

  /** Function generating the list of scripts from a map from variables to a list of values */
  protected lazy val function: Function[Map[Variable, Seq[Value]], Seq[Sql]] = { variableToValues =>

    val variablesCount = using.size
    val orderedVariables = using.toSeq
    val orderedVariableValues = orderedVariables.map(variableToValues(_))
    val orderedVariableValuesCount = orderedVariableValues.map(_.size)

    /* If at least one of the variables has no value, then the script cannot be executed */
    if (orderedVariableValuesCount.contains(0)) Seq.empty
    else {
      IncrementalTupleIterator(orderedVariableValuesCount).toSeq map { valueChoices =>
        val orderedValues = Seq.tabulate(variablesCount) { i =>
          orderedVariableValues(i)(valueChoices(i))
        }
        val mapping = orderedVariables.zip(orderedValues).toMap
        apply(mapping)
      }
    }
  }

}

case class ScriptUp(content: Map[Variable, Value] => Sql, using: Set[Variable]) extends ScriptFilePart {
  def toEvolutionPart = EvolutionUp(function)
}

case class ScriptDown(content: Map[Variable, Value] => Sql, using: Set[Variable]) extends ScriptFilePart {
  def toEvolutionPart = EvolutionDown(function)
}

case class ScriptDefine(variable: Variable, content: Map[Variable, Value] => Sql, using: Set[Variable]) extends ScriptFilePart {
  def toEvolutionPart = EvolutionDefine(variable, function)
}
