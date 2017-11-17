package darwin.db.drivers.oracle

import darwin.db.drivers.DefaultScriptQueries

class ScriptQueries extends DefaultScriptQueries {

  val CreatePlayEvolutionsOracleSql =
    """
      CREATE TABLE ${schema}play_evolutions (
          id Number(10,0) Not Null Enable,
          hash VARCHAR2(255 Byte),
          applied_at Timestamp Not Null,
          apply_script clob,
          revert_script clob,
          state Varchar2(255),
          last_problem clob,
          CONSTRAINT play_evolutions_pk PRIMARY KEY (id)
      )
    """
}
