package darwin

import darwin.Evolution.Values
import play.api.libs.Collections

/**
  * Created by gael on 26/09/17.
  */
object ScriptEngine {

  def prepare(script: Script): Either[String, Evolution] = {
    val definesWithVariables = script.defines map { case (v, d) => (v, d, extractVariables(d)) }
    val upsWithVariables = script.ups map { u => (u, extractVariables(u)) }
    val downsWithVariables = script.downs map { d => (d, extractVariables(d)) }

    val allVariables = definesWithVariables.flatMap(_._3) ++ upsWithVariables.flatMap(_._2) ++ downsWithVariables.flatMap(_._2) toSet
    val definedVariables = script.defines.map(_._1).toSet

    val missingVariables = allVariables -- definedVariables

    /* Check variables used within defines have been initialized before */
    val (usedBeforeDefinition, _) =
      definesWithVariables.foldLeft((Set.empty[String], Set.empty[String])) {
        case ((missing, found), (variable, define, dependentVariables)) =>
          val m = dependentVariables -- found
          (missing ++ m, found + variable)
      }

    if (missingVariables.nonEmpty) Left(s"Missing variables: ${missingVariables.mkString(", ")}")
    else if (usedBeforeDefinition.nonEmpty) Left(s"Variables used before they were defined: ${missingVariables.mkString(", ")}")
    else {
      val ps = (prepareScript _).tupled
      Right(Evolution(
        script.revision,
        definesWithVariables map { case (variable, script, extracted) => (variable, prepareScript(script, extracted)) },
        upsWithVariables map ps,
        downsWithVariables map ps
      ))
    }
  }

  private val VariableMarker = """{\w+}""".r

  private def extractVariables(query: String) = VariableMarker.findAllIn(query).toSet

  private def prepareScript(script: String, variables: Set[String]): Function[Values, Seq[String]] = { values =>

    val variablesCount = variables.size
    val orderedVariables = variables.toSeq
    val orderedVariableValues = orderedVariables map { v => values(v) }
    val orderedVariableValuesCount = orderedVariableValues.map(_.size)

    /* If a variable has not value, then no script can be executed if it is needed */
    if (orderedVariableValuesCount.contains(0)) Seq.empty
    else Collections.unfoldLeft(PositionIterator(orderedVariableValuesCount)) { positionIt =>
      positionIt.next map { newPosition =>
        val orderedValues = Seq.tabulate(variablesCount) { i => orderedVariableValues(i)(positionIt(i)) }
        val mapping = orderedVariables zip orderedValues
        val replaced = mapping.foldLeft(script) { case (script, (variable, value)) =>
          script.replace(s"{$variable}", value)
        }
        (newPosition, replaced)
      }
    }

  }

}
