package darwin.db

import darwin.Configuration

import scala.concurrent.ExecutionContext

/**
  * Created by gael on 22/10/17.
  */
class Transaction(dbName: Option[String])(implicit conf: Configuration) {

  private[db] var connection: JdbcConnection = null

  private var status = 'not_started

  def start()(implicit ec: ExecutionContext): Unit = {
    connection = JdbcConnection(dbName)
    status = 'started
  }

  def commit()(implicit ec: ExecutionContext): Unit = {
    if (connection == null) throw new IllegalStateException("Transaction not started")
    connection.commit()
    connection.close()
    status = 'committed
  }

  def rollback()(implicit ec: ExecutionContext): Unit = {
    if (connection == null) throw new IllegalStateException("Transaction not started")
    connection.rollback()
    connection.close()
    status = 'rollbacked
  }

  def started: Boolean = status == 'started

  def terminated: Boolean = status == 'committed || status == 'rollbacked

}
