package darwin

import javax.inject.Singleton

import com.google.inject.AbstractModule
import darwin.files.{FilesLocator, ScriptFileParser}
import darwin.model.Revision

import scala.collection.Set

/**
  * @author Gaël Renoux
  */
class DarwinModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[Darwin]).asEagerSingleton()
}

@Singleton
class Darwin(locator: FilesLocator[Revision], fileParser: ScriptFileParser, scriptParser: ScriptParser, dbName: String) {

  val files = locator.paths(dbName)

  val fileContents = files map { case (revision, src) =>
    (revision, src.getLines())
  }

  val scripts = fileContents map (fileParser.parse _).tupled
  files.foreach(_._2.close())

  //TODO validate

  val evolutions = scripts.map(scriptParser.parse)


}