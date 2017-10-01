package darwin

import play.api.Logger

/**
  * Created by gael on 26/09/17.
  */
class ScriptFileParser {

  private val log = Logger(classOf[ScriptFileParser])

  private val UpMarker = """^#.*!Up.*$""".r
  private val DownMarker = """^#.*!Down.*$""".r
  private val DefineMarker = """^#.*!Define (.\w*).*$""".r
  private val CommentMarker = """^#.*$""".r

  private trait ReadingState

  private object UnknownState extends ReadingState

  private object UpState extends ReadingState

  private object DownState extends ReadingState

  private case class DefineState(variable: String) extends ReadingState

  private def integrate(script: Script, state: ReadingState, lines: Seq[String]): Script = {
    val text = lines.mkString("\n")
    state match {
      case UnknownState => script
      case UpState => script.copy(ups = script.ups :+ text)
      case DownState => script.copy(downs = script.downs :+ text)
      case DefineState(variable) => script.copy(defines = script.defines :+ (variable -> text))
    }
  }

  def parse(revision: Revision, lines: TraversableOnce[String]): Script = {
    val (lastState, script, lastAccum) =
      lines.filter(_.trim.nonEmpty).foldLeft((UnknownState: ReadingState, Script(revision), Seq.empty[String])) {
        /* State changes */
        case ((state, script, accum), line@UpMarker()) =>
          println(s"UpMarker: $line")
          (UpState, integrate(script, state, accum), Seq.empty)
        case ((state, script, accum), line@DownMarker()) =>
          println(s"DownMarker: $line")
          (DownState, integrate(script, state, accum), Seq.empty)
        case ((state, script, accum), line@DefineMarker(variable)) =>
          println(s"DefineMarker: $line")
          (DefineState(variable), integrate(script, state, accum), Seq.empty)

        /* Ignore comment lines */
        case (stateScriptAccum, line@CommentMarker()) =>
          println(s"CommentMarker: $line")
          stateScriptAccum

        /* Reading from the file */
        case ((state, script, accum), line) =>
          println(s"Reading in $state: $line")
          (state, script, accum :+ line)
      }

    integrate(script, lastState, lastAccum)
  }

}
