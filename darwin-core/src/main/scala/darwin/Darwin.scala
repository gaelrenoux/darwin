package darwin

import com.typesafe.config.ConfigFactory
import darwin.model.{Revision, Status}

import scala.concurrent.Future

/**
  * Entry point.
  */
class Darwin {
  val conf = new Configuration(ConfigFactory.load)

  /** Returns the revisions that must be unapplied and applied. */
  def verify(): Future[Status] = ???

  /** If there are only upgrades to execute, execute them, then returns the revisions applied in a Status in a Right.
    * If there are downgrades to execute, does not modify the database and returns a Status in a Left. */
  /** Execute whatever is needed to put the database in good shape. Returns a Status with what was done. */
  def execute(): Future[Map[String, Status]] = if (!conf.enabled) Future.successful(Map()) else {
    conf.db.all
    //TODO

  }

}
