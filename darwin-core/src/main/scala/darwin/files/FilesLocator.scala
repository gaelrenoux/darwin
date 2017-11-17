package darwin.files

import darwin.model.Revision

import scala.io.BufferedSource


/** Traits for way to find an ordered series of files for SQL codes corresponding to each version. */
trait FilesLocator[+T <: Revision] {

  /** Returns the content of the files to parse, ordered by their revision. */
  def paths(dbName: String): Seq[(T, BufferedSource)]

}
