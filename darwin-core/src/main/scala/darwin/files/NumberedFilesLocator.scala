package darwin.files

import darwin.model.NumberedRevision
import darwin.util.Collections

import scala.io.{BufferedSource, Codec, Source}

/** Gets files by following natural ordering: 1.sql, 2.sql, etc. Any missing file means the sequence will stop
  * there (i.e., if there are files 1.sql, 2.sql and 4.sql, only 1.sql and 2.sql will be returned. */
class NumberedFilesLocator extends FilesLocator[NumberedRevision] {

  lazy val classLoader: ClassLoader = classOf[NumberedFilesLocator].getClassLoader

  override def paths(dbName: String): Seq[(NumberedRevision, BufferedSource)] =
    Collections.unfoldLeft(1) { revision =>
      loadResource(dbName, revision) map { stream =>
        (revision + 1, (NumberedRevision(revision), Source.fromInputStream(stream)(Codec.UTF8)))
      }
    } sortBy (_._1)

  private def loadResource(dbName: String, revision: Int) =
    Option(classLoader.getResourceAsStream(s"darwin/${dbName}/${revision}.sql"))

}
