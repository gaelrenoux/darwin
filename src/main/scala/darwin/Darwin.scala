package darwin

import javax.inject.Singleton

import com.google.inject.AbstractModule

import scala.io.{Codec, Source}

/**
  * @author Gaël Renoux
  */
class DarwinModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[Darwin]).asEagerSingleton()
}

@Singleton
class Darwin(locator: FilesLocator[Revision], parser: ScriptFileParser, dbName: String) {

  val files = locator.paths(dbName)

  val fileContents = files map { case (revision, src) =>
    (revision, src.getLines())
  }

  val scripts = fileContents map (parser.parse _).tupled
  files.foreach(_._2.close())

  val (errorLefts, evolutionRights) = scripts map EvolutionFactory.prepare partition(_.isLeft)
  if (errorLefts.nonEmpty) throw new IllegalArgumentException(errorLefts.map(_.left.get).mkString("\n"))

  val evolutions = evolutionRights.map(_.right.get)


}