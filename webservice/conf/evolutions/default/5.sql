# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS subject;

CREATE TABLE subject (
  id            int(11)         NOT NULL AUTO_INCREMENT,
  name          varchar(100)    NOT NULL DEFAULT '',
  description   varchar(1000)   NOT NULL DEFAULT '',

  PRIMARY KEY (id),
  UNIQUE KEY subject_uk_name (name)
);

# --- !Downs

DROP TABLE IF EXISTS subject;