package darwin.db

import java.sql.SQLWarning

import com.typesafe.scalalogging.Logger
import darwin.Configuration
import darwin.model.Sql

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by gael on 22/10/17.
  */
class SqlExecutor(dbName: Option[String])(implicit conf: Configuration) {

  private val log = Logger[SqlExecutor]

  /** Execute a SELECT query returning a String */
  def selectString(sql: Sql)(implicit ec: ExecutionContext, transaction: Transaction = new Transaction(dbName)): Future[Seq[String]] =
    transaction.connection.select[String](sql.wrapped)(_.getString(1))

  /** Execute a SELECT query returning an Seq of String */
  def selectStringSeq(sql: Sql)(implicit ec: ExecutionContext, transaction: Transaction = new Transaction(dbName)): Future[Seq[Seq[String]]] =
    transaction.connection.select[Seq[String]](sql.wrapped) { rs =>
      val c = rs.getMetaData.getColumnCount
      for (i <- 1 to c) yield rs.getString(i)
    }

  /** Executes an arbitrary UPDATE query */
  def update(sql: Sql)(implicit ec: ExecutionContext, transaction: Transaction = new Transaction(dbName)): Future[Int] = {
    transaction.connection.update(sql.wrapped)
  }

  def getWarnings(implicit ec: ExecutionContext, transaction: Transaction): Future[List[SQLWarning]] =
    transaction.connection.getWarnings

  /** Declare everything inside the block is in a transaction, and gives access to the transaction object. If the
    * future fails or the block throws an exception, it will rollback. In other cases, it will commit.
    *
    * If a transaction is implicitly available, it will use this one instead. In this case, it will not commit or
    * rollback, living that job to the outer transaction. */
  def transactional[A](treatment: Transaction => Future[A])
                      (implicit ec: ExecutionContext, transaction: Transaction = new Transaction(dbName)): Future[A] = {
    val isNew = !transaction.started

    if (isNew) {
      transaction.start()
    }

    val handleFailure: PartialFunction[Throwable, Nothing] = {
      case th =>
        if (isNew) try transaction.rollback() catch {
          case th2: Throwable => log.error("Error while rollbacking because of error", th2)
        }
        throw th
    }

    /* Call handle failure on exception thrown or failure of the future*/
    try {
      treatment(transaction) map { res =>
        if (isNew) transaction.commit()
        res
      } recover handleFailure
    } catch handleFailure
  }
}
