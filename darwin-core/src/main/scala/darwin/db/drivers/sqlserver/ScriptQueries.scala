package darwin.db.drivers.sqlserver

import darwin.db.drivers.DefaultScriptQueries

class ScriptQueries extends DefaultScriptQueries {

  val CreatePlayEvolutionsSqlServerSql =
    """
      create table ${schema}play_evolutions (
          id int not null primary key,
          hash varchar(255) not null,
          applied_at datetime not null,
          apply_script text,
          revert_script text,
          state varchar(255),
          last_problem text
      )
    """
}
