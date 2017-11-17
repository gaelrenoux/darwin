package darwin.integration

import darwin.files.{FilesLocator, ScriptFileParser, ScriptParser}
import darwin.model.Revision

/*

import javax.inject.Singleton

import com.google.inject.AbstractModule

class DarwinModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[Darwin]).asEagerSingleton()
}

@Singleton
*/
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