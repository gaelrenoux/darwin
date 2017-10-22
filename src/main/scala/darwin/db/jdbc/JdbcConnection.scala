package darwin.db.jdbc

import java.sql.{DriverManager, ResultSet, SQLWarning, Statement}
import java.util.Properties

import darwin.Configuration

import scala.concurrent.{ExecutionContext, Future}

/** Connection to the database. Entry point for all SQL queries. Can only be used within the jdbc package. */
private[jdbc] class JdbcConnection(private[jdbc] val connection: java.sql.Connection) {

  def commit(): Unit = connection.commit()

  def rollback(): Unit = connection.rollback()

  def close(): Unit = {
    connection.rollback()
    connection.close()
  }

  def getWarnings: List[SQLWarning] = JdbcConnection.collect(Option(connection.getWarnings.getNextWarning))

  def select[A](sql: String)(process: ResultSet => A)(implicit ec: ExecutionContext): Future[Seq[A]] = withStatement { st =>
    Future(st.executeQuery(sql)) map JdbcConnection.processResultSet(process)
  }

  def update(sql: String)(implicit ec: ExecutionContext): Future[Int] = withStatement { st =>
    Future(st.executeUpdate(sql))
  }

  private def withStatement[A](block: Statement => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    val statement = connection.createStatement
    try {
      val res = block(statement)
      res onComplete { _ => statement.close() }
      res
    } catch {
      case th: Throwable =>
        statement.close()
        throw th
    }
  }

}

object JdbcConnection {

  /* Load the database driver */
  Class.forName("com.mysql.jdbc.Driver")

  private[jdbc] def apply(db: Option[String])(implicit conf: Configuration) = {
    val dbConf = db.map(conf.db.named).getOrElse(conf.db.default)

    val props = new Properties()
    props.setProperty("user", dbConf.user)
    props.setProperty("password", dbConf.password)
    props.setProperty("allowMultiQueries", "true");

    val con = DriverManager.getConnection(dbConf.url, props)
    con.setAutoCommit(false)

    new JdbcConnection(con)
  }


  private def collect[A](block: => Option[A], acc: List[A] = Nil): List[A] = block match {
    case None => acc.reverse
    case Some(x) => collect(block, x :: acc)
  }

  private def processResultSet[A](process: ResultSet => A, acc: List[A] = Nil)(rs: ResultSet): List[A] =
    if (rs.next()) {
      processResultSet(process, process(rs) :: acc)(rs)
    } else acc.reverse

}