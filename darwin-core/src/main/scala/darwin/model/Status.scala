package darwin.model

/**
  * What is needed to bring the database to the correct level.
  */
case class Status(downgrades: Seq[Revision], upgrades: Seq[Revision]) {
  def isWorkNeeded: Boolean = downgrades.nonEmpty && upgrades.nonEmpty
}
