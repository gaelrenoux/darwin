package darwin.db.jdbc

import java.sql.SQLWarning

import com.typesafe.scalalogging.Logger
import darwin.Configuration
import darwin.db.{SqlExecutor, Transaction}
import darwin.model.Sql

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by gael on 22/10/17.
  */
class JdbcSqlExecutor(db: Option[String])(implicit conf: Configuration) extends SqlExecutor[JdbcTransaction] {

  private val log = Logger[JdbcSqlExecutor]

  override protected def createTransaction: JdbcTransaction = new JdbcTransaction(db)

  override def transactional[A](treatment: JdbcTransaction => Future[A])(implicit ec: ExecutionContext, transaction: JdbcTransaction = createTransaction): Future[A] = {
    val isNew = !transaction.started

    if (isNew) {
      transaction.start
    }

    val handleFailure: PartialFunction[Throwable, Nothing] = {
      case th =>
        if (isNew) try transaction.rollback catch {
          case th2: Throwable => log.error("Error while rollbacking because of error", th2)
        }
        throw th
    }

    try {
      treatment(transaction) map { res =>
        if (isNew) transaction.commit
        res
      } recover handleFailure
    } catch handleFailure
  }

  /** Execute an arbitrary SELECT query */
  override def selectString(sql: Sql)(implicit ec: ExecutionContext, transaction: JdbcTransaction = createTransaction): Future[Seq[String]] =
    transaction.connection.select[String](sql.wrapped)(_.getString(1))

  /** Execute an arbitrary SELECT query */
  override def selectStringSeq(sql: Sql)(implicit ec: ExecutionContext, transaction: JdbcTransaction = createTransaction): Future[Seq[Seq[String]]] =
    transaction.connection.select[Seq[String]](sql.wrapped) { rs =>
      val c = rs.getMetaData.getColumnCount
      for (i <- 1 to c) yield rs.getString(i)
    }


  /** Executes an arbitrary UPDATE query */
  override def update(sql: Sql)(implicit ec: ExecutionContext, transaction: JdbcTransaction = createTransaction): Future[Int] = ???

  def getWarnings(implicit t: JdbcTransaction): List[SQLWarning] = t.connection.getWarnings

}
