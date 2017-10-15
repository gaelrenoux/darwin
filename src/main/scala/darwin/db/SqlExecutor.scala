package darwin.db

import darwin.model.Sql

trait SqlExecutor {

  /** Executes an update request (up or down) on the database */
  def executeUpdate(sql: Sql): Int

  /** Executes a define request on the database */
  def executeDefine(sql: Sql): String
}
