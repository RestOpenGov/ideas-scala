# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS subject;

CREATE TABLE subject (
  id            int(11)         NOT NULL AUTO_INCREMENT,
  name          varchar(100)    DEFAULT NULL,
  description   varchar(1000)   DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY subject_uk_name (name)
);

# --- !Downs

DROP TABLE IF EXISTS subject;