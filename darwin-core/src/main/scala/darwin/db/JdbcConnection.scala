package darwin.db

import java.sql.{DriverManager, ResultSet, SQLWarning, Statement}
import java.util.Properties

import darwin.Configuration

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future, blocking}


/** Connection to the database. Entry point for all SQL queries. Can only be used within the jdbc package. */
private[db] class JdbcConnection(private val connection: java.sql.Connection) {

  def commit()(implicit ec: ExecutionContext): Future[Unit] = Future(blocking(connection.commit()))

  def rollback()(implicit ec: ExecutionContext): Future[Unit] = Future(blocking(connection.rollback()))

  def close()(implicit ec: ExecutionContext): Future[Unit] = Future(blocking {
    connection.rollback()
    connection.close()
  })

  def getWarnings(implicit ec: ExecutionContext): Future[List[SQLWarning]] = Future(blocking {
    collect(Option(connection.getWarnings.getNextWarning))
  })

  def select[A](sql: String)(process: ResultSet => A)(implicit ec: ExecutionContext): Future[Seq[A]] = Future(blocking {
    withStatement { st =>
      val rs = st.executeQuery(sql)
      processResultSet(process)(rs)
    }
  })

  def update(sql: String)(implicit ec: ExecutionContext): Future[Int] = Future(blocking {
    withStatement { st =>
      st.executeUpdate(sql)
    }
  })

  private def withStatement[A](block: Statement => A): A = {
    val statement = connection.createStatement
    try block(statement) finally {
      statement.close()
    }
  }

  @tailrec
  private def collect[A](block: => Option[A], acc: List[A] = Nil): List[A] = block match {
    case None => acc.reverse
    case Some(x) => collect(block, x :: acc)
  }

  @tailrec
  private def processResultSet[A](process: ResultSet => A, acc: List[A] = Nil)(rs: ResultSet): List[A] =
    if (rs.next()) {
      processResultSet(process, process(rs) :: acc)(rs)
    } else acc.reverse

}

object JdbcConnection {

  private[db] def apply(dbName: Option[String])(implicit conf: Configuration) = {
    val dbConf = dbName.map(conf.db.named).getOrElse(conf.db.default)

    val props = new Properties()
    props.setProperty("user", dbConf.user)
    props.setProperty("password", dbConf.password)
    props.setProperty("allowMultiQueries", "true");

    val con = DriverManager.getConnection(dbConf.url, props)
    con.setAutoCommit(false)

    new JdbcConnection(con)
  }

}