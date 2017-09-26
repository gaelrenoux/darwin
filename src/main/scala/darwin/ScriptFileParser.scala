package darwin

/**
  * Created by gael on 26/09/17.
  */
class ScriptFileParser {

  private val CommentMarker = """^#.*$""".r
  private val UpMarker = """^#.*!Up.*$""".r
  private val DownMarker = """^#.*!Down.*$""".r
  private val DefineMarker = """^#.*!Define (.\w*)\s.*$""".r

  private trait ReadingState

  private object UnknownState extends ReadingState

  private object UpState extends ReadingState

  private object DownState extends ReadingState

  private case class DefineState(variable: String) extends ReadingState

  private def integrate(script: Script, state: ReadingState, lines: Seq[String]): Script = {
    val text = lines.mkString("", "\n", "\n")
    state match {
      case UnknownState => script
      case UpState => script.copy(ups = script.ups :+ text)
      case DownState => script.copy(downs = script.downs :+ text)
      case DefineState(variable) => script.copy(defines = script.defines :+ (variable -> text))
    }
  }

  def parse(revision: String, lines: TraversableOnce[String]): Script = {
    lines.filter(_.trim.nonEmpty).foldLeft((UnknownState: ReadingState, Script(revision), Seq.empty[String])) {
      /* Ignore comment lines */
      case (stateScriptAccum, CommentMarker()) => stateScriptAccum

      /* State changes */
      case ((state, script, accum), UpMarker()) => (UpState, integrate(script, state, accum), Seq.empty)
      case ((state, script, accum), DownMarker()) => (DownState, integrate(script, state, accum), Seq.empty)
      case ((state, script, accum), DefineMarker(variable)) => (DefineState(variable), integrate(script, state, accum), Seq.empty)

      /* Reading from the file */
      case ((state, script, accum), line) => (state, script, accum :+ line)
    } _2
  }

}
