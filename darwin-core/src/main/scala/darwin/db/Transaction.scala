package darwin.db

/**
  * Created by gael on 22/10/17.
  */
trait Transaction {

  def start(): Unit

  def commit(): Unit

  def rollback(): Unit

  def started: Boolean

  def terminated: Boolean
}
