package darwin.db.jdbc

import java.sql.{DriverManager, ResultSet, SQLWarning, Statement}
import java.util.Properties

import darwin.Configuration
import darwin.model.Sql

import scala.annotation.tailrec


/** Connection to the database. Entry point for all SQL queries. Can only be used within the jdbc package. */
private[db] class JdbcConnection(private val connection: java.sql.Connection) {

  def commit(): Unit = connection.commit()

  def rollback(): Unit = connection.rollback()

  def close(): Unit = {
    connection.rollback()
    connection.close()
  }

  def getWarnings: List[SQLWarning] = {
    collect(Option(connection.getWarnings.getNextWarning))
  }

  /** Execute an arbirtrary SELECT query, with a process to convert the ResultSet to an A */
  def select[A](sql: String)(process: ResultSet => A): Seq[A] = {
    withStatement { st =>
      val rs = st.executeQuery(sql)
      processResultSet(process)(rs)
    }
  }

  /** Execute a SELECT query returning a String */
  def selectString(sql: Sql): Seq[String] = select[String](sql.wrapped)(_.getString(1))

  /** Execute a SELECT query returning an Seq of String */
  def selectStringSeq(sql: Sql): Seq[Seq[String]] = select[Seq[String]](sql.wrapped) { rs =>
    val c = rs.getMetaData.getColumnCount
    for (i <- 1 to c) yield rs.getString(i)
  }

  def update(sql: String): Int = {
    withStatement { st =>
      st.executeUpdate(sql)
    }
  }

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