package darwin.db

import darwin.db.drivers.ScriptQueries
import darwin.db.jdbc.TransactionManager

import scala.concurrent.{ExecutionContext, Future, blocking}

/**
  *
  * Created by gael on 22/10/17.
  */
class DarwinTablesDao(tm: TransactionManager, queries: ScriptQueries) {
  /* TODO */
  def ensureTables()(implicit ec: ExecutionContext): Future[Unit] = Future(blocking {
    tm.transactional { t =>
      val con = t.connection

      con.update(queries.Create.wrapped)
      con.update(queries.CreatePart.wrapped)

    }
  })


}
