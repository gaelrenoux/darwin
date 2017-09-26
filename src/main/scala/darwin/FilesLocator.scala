package darwin

import java.io.InputStream


/**
  * Created by gael on 26/09/17.
  */
trait FilesLocator {

  def paths(dbName: String): Seq[(String, InputStream)]

}
