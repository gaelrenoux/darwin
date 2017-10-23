package darwin.db

import darwin.model.Sql

import scala.concurrent.{ExecutionContext, Future}


trait SqlExecutor[T <: Transaction] {

  protected def createTransaction: T

  def transactional[A](treatment: T => Future[A])(implicit ec: ExecutionContext, transaction: T = createTransaction): Future[A]

  /** Execute a SELECT query returning a String */
  def selectString(sql: Sql)(implicit ec: ExecutionContext, transaction: T): Future[Seq[String]]

  /** Execute a SELECT query returning an Seq of String */
  def selectStringSeq(sql: Sql)(implicit ec: ExecutionContext, transaction: T): Future[Seq[Seq[String]]]

  /** Executes an arbitrary UPDATE query */
  def update(sql: Sql)(implicit ec: ExecutionContext, transaction: T): Future[Int]
}
