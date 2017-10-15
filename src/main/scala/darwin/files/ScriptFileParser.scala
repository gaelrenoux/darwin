package darwin.files

import darwin._
import darwin.model.{Revision, Sql, Value, Variable}
import play.api.Logger

/**
  * Parses a script file and returns a Script object, with separated elements.
  * Created by gael on 26/09/17.
  */
class ScriptFileParser {

  private val log = Logger(classOf[ScriptFileParser])

  private val UpMarker = """^#.*!Up.*$""".r
  private val DownMarker = """^#.*!Down.*$""".r
  private val DefineMarker = """^#.*!Define (.\w*).*$""".r
  private val CommentMarker = """^#.*$""".r
  private val VariableMarker = """\$\{(\w+)\}""".r

  private trait ReadingState
  private object ReadingState {
    object Unknown extends ReadingState
    object Up extends ReadingState
    object Down extends ReadingState
    case class Define(variable: Variable) extends ReadingState
  }

  /** Given a script file, get a new ScriptFile with the new data */
  private def integrate(script: ScriptFile, state: ReadingState, lines: Seq[String]): ScriptFile = {
    val fileText = lines.mkString("\n")
    val usedVariables = VariableMarker.findAllMatchIn(fileText) map { m =>
      Variable(m.group(1))
    } toSet

    /* For a certain set of variable values, returns the script to execute */
    def fromVariablesToScript(values: Map[Variable, Value]): Sql = {
      val filledText = values.foldLeft(fileText) { case (text, (variable, value)) =>
        text.replace("${" + variable.name +"}", value.wrapped)
      }
      Sql(filledText)
    }

    state match {
      case ReadingState.Unknown => script // ignoring text before comments defining up, down or define
      case ReadingState.Up => script :+ ScriptUp(fromVariablesToScript, usedVariables)
      case ReadingState.Down => script :+ ScriptDown(fromVariablesToScript, usedVariables)
      case ReadingState.Define(variable) => script :+ ScriptDefine(variable, fromVariablesToScript, usedVariables)
    }
  }

  def parse(revision: Revision, lines: TraversableOnce[String]): ScriptFile = {
    val nonEmptyLines = lines.filter(_.trim.nonEmpty)
    val (lastState, scriptFileMissingLastPart, lastAccum) =

      nonEmptyLines.foldLeft((ReadingState.Unknown: ReadingState, ScriptFile(revision), Seq.empty[String])) {
        /* State changes */
        case ((state, script, accum), UpMarker()) =>
          (ReadingState.Up, integrate(script, state, accum), Seq.empty)
        case ((state, script, accum), DownMarker()) =>
          (ReadingState.Down, integrate(script, state, accum), Seq.empty)
        case ((state, script, accum), DefineMarker(variableName)) =>
          (ReadingState.Define(Variable(variableName)), integrate(script, state, accum), Seq.empty)

        /* Ignore comment lines */
        case (stateScriptAccum, CommentMarker()) =>
          stateScriptAccum

        /* Reading from the file */
        case ((state, script, accum), line) =>
          (state, script, accum :+ line)
      }

    /* get the last line */
    integrate(scriptFileMissingLastPart, lastState, lastAccum)
  }



}
