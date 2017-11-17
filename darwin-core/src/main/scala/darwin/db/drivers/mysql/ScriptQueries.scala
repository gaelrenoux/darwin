package darwin.db.drivers.mysql

import darwin.db.drivers.DefaultScriptQueries

class ScriptQueries extends DefaultScriptQueries {

  val CreatePlayEvolutionsMySql =
    """
      CREATE TABLE ${schema}play_evolutions (
          id int not null primary key,
          hash varchar(255) not null,
          applied_at timestamp not null,
          apply_script mediumtext,
          revert_script mediumtext,
          state varchar(255),
          last_problem mediumtext
      )
    """
}
