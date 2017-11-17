package darwin.db.drivers

import darwin.model.Sql

abstract class DefaultScriptQueries {

  val Create = Sql(
    """
        create table __darwin_script
            revision varchar primary key,
            status varchar
      """)

  val CreatePart = Sql(
    """
        create table __darwin_script_part
            id serial primary key,
            revision varchar,
            order integer,
            type varchar,
            content varchar
      """)
}
