package darwin.db.jdbc

import com.typesafe.scalalogging.Logger
import darwin.Configuration

import scala.concurrent.ExecutionContext

class TransactionManager(dbName: Option[String])(implicit conf: Configuration) {

  private val log = Logger[TransactionManager]

  /** Declare everything inside the block is in a transaction, and gives access to the transaction object. If the
    * block throws an exception, it will rollback. In other cases, it will commit.
    *
    * If a transaction is implicitly available, it will use this one instead. In this case, it will not commit or
    * rollback, living that job to the outer transaction. */
  def transactional[A](treatment: Transaction => A)
                      (implicit ec: ExecutionContext, transaction: Transaction = new Transaction(dbName)): A = {
    val isNew = !transaction.started

    if (isNew) {
      transaction.start()
    }

    /* Call handle failure on exception thrown or failure of the future*/
    try {
      val res = treatment(transaction)
      if (isNew) transaction.commit()
      res
    } catch {
      case ex: Exception =>
        if (isNew) try transaction.rollback() catch {
          case th: Throwable => log.error("Error while rollbacking because of error", th)
        }
        throw ex
    }
  }
}
