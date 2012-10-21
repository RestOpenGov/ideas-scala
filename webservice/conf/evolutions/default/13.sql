# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS tag;

CREATE TABLE tag (
  id            int(11)         NOT NULL AUTO_INCREMENT,
  name          varchar(100)    NOT NULL DEFAULT '',
  description   varchar(1000)   NOT NULL DEFAULT '',
  created       timestamp       NOT NULL DEFAULT current_timestamp,

  PRIMARY KEY (id),
  UNIQUE KEY tag_uk_name (name)
);

# --- !Downs

DROP TABLE IF EXISTS tag;