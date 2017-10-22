package darwin.dao

import darwin.db.SqlExecutor
import darwin.model.Sql

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by gael on 22/10/17.
  */
trait ScriptDao {

  def createTables()(implicit ec: ExecutionContext): Future[Unit]



}
