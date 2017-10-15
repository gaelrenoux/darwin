package darwin.files

import darwin._
import darwin.model.{Revision, Sql, Value, Variable}
import play.api.Logger

/**
  * Parses a script file and returns a Script object, with separated elements.
  */
class ScriptFileParser {

  private val log = Logger(classOf[ScriptFileParser])

  private val UpMarker = """^#.*!Up.*$""".r
  private val DownMarker = """^#.*!Down.*$""".r
  private val DefineMarker = """^#.*!Define (.\w*).*$""".r
  private val CommentMarker = """^#.*$""".r
  private val VariableMarker = """\$\{(\w+)\}""".r


  private case class ReadingVisitor private(
                                             status: Status,
                                             script: Script,
                                             currentContent: String,
                                             counter: Int
                                           ) {

    def this(status: Status, script: Script) = this(status, script, "", 0)

    def addLine(line: String): ReadingVisitor = copy(currentContent = currentContent + "\n" + line)

    def changeStatus(newStatus: Status): ReadingVisitor = {
      status match {
        case Status.Unknown =>
          new ReadingVisitor(newStatus, script) // ignoring text before comments defining up, down or define
        case Status.Up =>
          ReadingVisitor(newStatus, script :+ ScriptUp(counter, currentContent.trim), "", counter + 1)
        case Status.Down =>
          ReadingVisitor(newStatus, script :+ ScriptDown(counter, currentContent.trim), "", counter + 1)
        case Status.Define(variable) =>
          ReadingVisitor(newStatus, script :+ ScriptDefine(counter, variable, currentContent.trim), "", counter + 1)
      }
    }

    def terminate: Script = this.changeStatus(Status.Unknown).script
  }

  private trait Status

  private object Status {

    object Unknown extends Status

    object Up extends Status

    object Down extends Status

    case class Define(variable: Variable) extends Status

  }

  def parse(revision: Revision, lines: TraversableOnce[String]): Script = {
    val nonEmptyLines = lines.filter(_.trim.nonEmpty)
    val finishedVisitor = nonEmptyLines.foldLeft(new ReadingVisitor(Status.Unknown, Script(revision))) {
      /* State changes */
      case (visitor, UpMarker()) => visitor.changeStatus(Status.Up)
      case (visitor, DownMarker()) => visitor.changeStatus(Status.Down)
      case (visitor, DefineMarker(variableName)) => visitor.changeStatus(Status.Define(Variable(variableName)))

      /* Ignore comment lines */
      case (visitor, CommentMarker()) => visitor

      /* Reading from the file */
      case (visitor, line) => visitor.addLine(line)
    }

    finishedVisitor.terminate
  }

}

object ScriptFileParser {
  def textToMappingToSql(fileText: String)(values: Map[Variable, Value]): Sql = {
    val filledText = values.foldLeft(fileText) { case (text, (variable, value)) =>
      text.replace("${" + variable.name + "}", value.wrapped)
    }
    Sql(filledText)
  }
}
