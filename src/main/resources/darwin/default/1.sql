# --- !Define sample
select distinct stuff from thing

# --- !Up Do some stuff
alter table ${sample}.blabla add test integer;

# --- !Down Do some stuff
alter table ${sample}.blabla drop test;

