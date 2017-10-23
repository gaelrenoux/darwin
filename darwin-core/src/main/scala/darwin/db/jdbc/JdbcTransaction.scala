package darwin.db.jdbc

import darwin.Configuration
import darwin.db.Transaction

class JdbcTransaction(db: Option[String])(implicit conf: Configuration) extends Transaction {

  private[jdbc] var connection: JdbcConnection = null

  private var status = 'not_started

  override def start(): Unit = {
    connection = JdbcConnection(db)
    status = 'started
  }

  override def commit(): Unit = {
    if (connection == null) throw new IllegalStateException("Transaction not started")
    connection.commit()
    connection.close()
    status = 'committed
  }

  override def rollback(): Unit = {
    if (connection == null) throw new IllegalStateException("Transaction not started")
    connection.rollback()
    connection.close()
    status = 'rollbacked
  }

  override def started: Boolean = status == 'started

  override def terminated: Boolean = status == 'committed || status == 'rollbacked

}
