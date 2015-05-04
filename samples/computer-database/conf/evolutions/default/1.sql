# --- !Ups

create sequence company_id_seq
  start with 1
  increment by 1
  no minvalue
  no maxvalue
  cache 1;

create table "company" (
  id integer not null default nextval('company_id_seq'),
  name char varying(255) not null
);


create sequence computer_id_seq
  start with 1
  increment by 1
  no minvalue
  no maxvalue
  cache 1;

create table "computer" (
  id integer not null default nextval('computer_id_seq'),
  name char varying(255) not null,
  introduced integer,
  discontinued integer,
  company_id, integer
);

# --- !Downs

drop table "computer";
drop table "company";

