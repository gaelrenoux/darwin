package darwin.files

import java.io.File

import darwin.model.VersionedRevision

import scala.io.{BufferedSource, Codec, Source}

/** Get files named after the typical versioning scheme: A.B.C-Descriptor.sql, with an arbitrary nuber of numbers and a
  * arbitrary descriptor. */
class VersionedFilesLocator extends FilesLocator[VersionedRevision] {

  private lazy val classLoader = classOf[VersionedFilesLocator].getClassLoader

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
