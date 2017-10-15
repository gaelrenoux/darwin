package darwin

import darwin.model.{Sql, Value, Variable}
import darwin.util.IncrementalTupleIterator
import play.api.Logger

/**
  * Parses a script part and returns an Evolution object.
  */
class ScriptParser {

  private val log = Logger(classOf[ScriptParser])

  private val VariableMarker = """\$\{(\w+)\}""".r

  def parse(script: Script): Evolution = {
    val evoParts = script.parts map parsePart
    Evolution(script.revision, evoParts)
  }

  def parsePart(part: ScriptPart): EvolutionPart = {
    val usedVariables = VariableMarker.findAllMatchIn(part.content).map(_.group(1)).map(Variable.apply).toSet
    val sqlGeneration = ScriptParser.textToMappingToSql(part.content) _
    val multipleSqlGeneration = applyForMultipleValues(sqlGeneration, usedVariables) _

    part match {
      case ScriptUp(_, _) => EvolutionUp(multipleSqlGeneration)
      case ScriptDown(_, _) => EvolutionDown(multipleSqlGeneration)
      case ScriptDefine(_, variable, _) => EvolutionDefine(variable, multipleSqlGeneration)
    }
  }

  private def applyForMultipleValues(
                                      generatorFunction: Map[Variable, Value] => Sql,
                                      usedVariables: Set[Variable]
                                    )(
                                      variableToValues: Map[Variable, Seq[Value]]
                                    ): Seq[Sql] = {

    val variablesCount = usedVariables.size
    val orderedVariables = usedVariables.toSeq
    val orderedVariableValues = orderedVariables.map(variableToValues(_))
    val orderedVariableValuesCount = orderedVariableValues.map(_.size)

    /* If there is no variable, just apply the function to an empty map */
    if (variablesCount == 0) Seq(generatorFunction(Map()))
    /* If at least one of the variables has no value, then the script cannot be executed */
    else if (orderedVariableValuesCount.contains(0)) Seq.empty
    /* If there are variables with values, start iterating */
    else {
      IncrementalTupleIterator(orderedVariableValuesCount).toSeq map { valueChoices =>
        val orderedValues = Seq.tabulate(variablesCount) { i =>
          orderedVariableValues(i)(valueChoices(i))
        }
        val mapping = orderedVariables.zip(orderedValues).toMap
        generatorFunction(mapping)
      }
    }
  }

}

object ScriptParser {

  /** Takes a text, then a set of values (associated to variables), and return the appropriate SQL. */
  def textToMappingToSql(fileText: String)(values: Map[Variable, Value]): Sql = {
    val filledText = values.foldLeft(fileText) { case (text, (variable, value)) =>
      text.replace("${" + variable.name + "}", value.wrapped)
    }
    Sql(filledText)
  }
}

