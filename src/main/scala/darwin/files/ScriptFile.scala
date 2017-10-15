package darwin.files

import darwin._
import darwin.model.{Revision, Variable}

/** This is a script file, with its elements in order. */
case class ScriptFile(
                       revision: Revision,
                       parts: Seq[ScriptPart] = Seq()
                     ) {

  import ScriptFile._

  def :+(part: ScriptPart): ScriptFile = copy(parts = parts :+ part)

  def toEvolution = Evolution(revision, parts.map(_.toEvolutionPart))

  /** Returns all validation errors on this script file. */
  def validate: Set[Error] = {
    val (allErrors, _, _) = parts.foldLeft(Set[Error](), Set[Variable](), Set[Variable]()) {
      case ((errors, usedVariables, availableVariables), part) =>

        val missingVariables = part.using -- availableVariables
        val additionalErrors = missingVariables.map(_.name).map(Error.MissingVariable.apply)
        val additionalVariable = part match {
          case ScriptDefine(variable, _, _) => Some(variable)
          case _ => None
        }
        (errors ++ additionalErrors, usedVariables ++ part.using, availableVariables ++ additionalVariable)
    }
    allErrors
  }

}

object ScriptFile {

  abstract class Error(val key: String, val message: String)

  object Error {

    case class MissingVariable(name: String) extends Error(s"variable.missing", s"Missing variable $name (it should be defined before use)")

  }

}
