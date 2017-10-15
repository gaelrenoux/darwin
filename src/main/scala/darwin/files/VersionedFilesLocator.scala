package darwin.files

import java.io.File

import darwin.Darwin
import darwin.model.VersionedRevision

import scala.io.{BufferedSource, Codec, Source}

/**
  * Created by gael on 26/09/17.
  */
class VersionedFilesLocator extends FilesLocator[VersionedRevision] {

  private lazy val classLoader = classOf[Darwin].getClassLoader

  override def paths(dbName: String): Seq[(VersionedRevision, BufferedSource)] = {
    val folderPath = classLoader.getResource(s"darwin/$dbName")
    val folder = new File(folderPath.getPath)
    val files = if (folder.exists && folder.isDirectory) folder.listFiles.toSeq else Seq()

    files map { f =>
      val revision = VersionedRevision(f.getName.dropRight(4)) //remove the .sql
      val src = Source.fromFile(f)(Codec.UTF8)
      (revision, src)
    } sortBy (_._1)
  }

}
