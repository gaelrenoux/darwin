package darwin
import java.io.InputStream

import play.api.libs.Collections

/**
  * Created by gael on 26/09/17.
  */
class NumberedFilesLocator extends FilesLocator {

  override def paths(dbName: String): Seq[(String, InputStream)] =
      Collections.unfoldLeft(1) { revision =>
        loadResource(dbName, revision) map { stream =>
          (revision + 1, (revision.toString, stream))
        }
      } sortBy (_._1)

  private def loadResource(dbName: String, revision: Int) = {
    val classLoader: ClassLoader = classOf[Darwin].getClassLoader
    Option(classLoader.getResourceAsStream(s"darwin/${dbName}/${revision}.sql"))
  }
}
