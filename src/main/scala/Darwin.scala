import javax.inject.Singleton

import com.google.inject.AbstractModule
import play.api.libs.Collections

import scala.io.{Codec, Source}

/**
  * @author Gaël Renoux
  */
class DarwinModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[Darwin]).asEagerSingleton()
}

@Singleton
class Darwin {

  private val CommentMarker = """^#.*$""".r
  private val UpMarker = """^#.*!Up.*$""".r
  private val DownMarker = """^#.*!Down.*$""".r
  private val DefineMarker = """^#.*!Define (.\w*)\s.*$""".r

  case class Script(
                     revision: Int,
                     ups: Seq[String] = Seq.empty,
                     downs: Seq[String] = Seq.empty,
                     defines: Map[String, String] = Map.empty
                   )

  private def loadResource(db: String, revision: Int) = {
    val classLoader: ClassLoader = classOf[Darwin].getClassLoader
    Option(classLoader.getResourceAsStream(s"darwin/${db}/${revision}.sql"))
  }

  private def evolutions(db: String): Seq[Script] = {
    trait ReadingState
    object UnknownState extends ReadingState
    object UpState extends ReadingState
    object DownState extends ReadingState
    case class DefineState(variable: String) extends ReadingState

    def integrate(script: Script, state: ReadingState, lines: Seq[String]): Script = {
      val text = lines.mkString("", "\n", "\n")
      state match {
        case UnknownState => script
        case UpState => script.copy(ups = script.ups :+ text)
        case DownState => script.copy(downs = script.downs :+ text)
        case DefineState(variable) => script.copy(defines = script.defines + (variable -> text))
      }
    }

    val revisionsAndLines = Collections.unfoldLeft(1) { revision =>
      loadResource(db, revision) map { stream =>
        (revision + 1, (revision, Source.fromInputStream(stream)(Codec.UTF8).getLines().filter(_.trim.nonEmpty)))
      }
    } sortBy (_._1)

    revisionsAndLines map { case (revision, lines) =>
      lines.foldLeft((UnknownState: ReadingState, Script(revision), Seq.empty[String])) {
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



  evolutions("darwin/default") foreach { script =>


  }


}