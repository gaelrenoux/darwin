package darwin

import java.io.InputStream

import scala.io.BufferedSource


/**
  * Created by gael on 26/09/17.
  */
trait FilesLocator[+T <: Revision] {

  def paths(dbName: String): Seq[(T, BufferedSource)]

}
