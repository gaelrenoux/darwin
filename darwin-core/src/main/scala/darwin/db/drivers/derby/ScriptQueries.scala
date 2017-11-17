package darwin.db.drivers.derby

import darwin.db.drivers.DefaultScriptQueries
import darwin.model.Sql

class ScriptQueries extends DefaultScriptQueries {
  override val Create: Sql = ???

  override val CreatePart: Sql = ???

  val CreatePlayEvolutionsDerby =
    """
      create table ${schema}play_evolutions (
          id int not null primary key,
          hash varchar(255) not null,
          applied_at timestamp not null,
          apply_script clob,
          revert_script clob,
          state varchar(255),
          last_problem clob
      )
    """
}
