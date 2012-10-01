# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS tag;

CREATE TABLE tag (
  id            int(11)         NOT NULL AUTO_INCREMENT,
  name          varchar(100)    DEFAULT NULL,
  description   varchar(1000)   DEFAULT NULL,
  created       timestamp       NULL DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY tag_uk_name (name)
);

# --- !Downs

DROP TABLE IF EXISTS tag;