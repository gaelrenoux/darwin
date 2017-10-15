package darwin

import java.io.InputStream

import scala.io.BufferedSource


/**
  * Created by gael on 26/09/17.
  */
trait FilesLocator[+T <: Revision] {

  /** Returns the files to parse, ordered by their revision. */
  def paths(dbName: String): Seq[(T, BufferedSource)]

}
