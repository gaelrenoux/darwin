package darwin.dao

import darwin.db.SqlExecutor
import darwin.model.Sql

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by gael on 22/10/17.
  */
class DarwinTablesDao(exe: SqlExecutor) {

  override def createTables()(implicit ec: ExecutionContext): Future[Unit] = exe.transactional { implicit t =>
    val scriptSql = Sql(
      """
        create table __darwin_script
            revision varchar primary key,
            status varchar
      """)
    val scriptPartSql = Sql(
      """
        create table __darwin_script_part
            id serial primary key,
            revision varchar,
            order integer,
            type varchar,
            content varchar
      """)
    for {
      i <- db.update(scriptSql)
      j <- db.update(scriptPartSql)
    } yield ()
  }


}
