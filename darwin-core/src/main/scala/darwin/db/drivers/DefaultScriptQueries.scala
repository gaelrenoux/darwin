package darwin.db.drivers

import darwin.model.Sql

abstract class DefaultScriptQueries extends ScriptQueries {

  override val Create = Sql(
    """
        create table __darwin_script
            revision varchar(255) primary key,
            status varchar(255)
      """)

  override val CreatePart = Sql(
    """
        create table __darwin_script_part
            id serial primary key,
            revision varchar(255),
            order int,
            type varchar(255),
            content text
      """)


  val CreatePlayEvolutionsSql =
    """
      create table ${schema}play_evolutions (
          id int not null primary key,
          hash varchar(255) not null,
          applied_at timestamp not null,
          apply_script text,
          revert_script text,
          state varchar(255),
          last_problem text
      )
    """



}




