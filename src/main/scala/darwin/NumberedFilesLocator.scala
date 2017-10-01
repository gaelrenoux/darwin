package darwin

import java.io.InputStream

import play.api.libs.Collections

import scala.io.{BufferedSource, Codec, Source}

/**
  * Created by gael on 26/09/17.
  */
class NumberedFilesLocator extends FilesLocator[NumberedRevision] {

  lazy val classLoader: ClassLoader = classOf[Darwin].getClassLoader

  override def paths(dbName: String): Seq[(NumberedRevision, BufferedSource)] =
    Collections.unfoldLeft(1) { revision =>
      loadResource(dbName, revision) map { stream =>
        (revision + 1, (NumberedRevision(revision), Source.fromInputStream(stream)(Codec.UTF8)))
      }
    } sortBy (_._1)

  private def loadResource(dbName: String, revision: Int) =
    Option(classLoader.getResourceAsStream(s"darwin/${dbName}/${revision}.sql"))

}
