# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS idea_type;

CREATE TABLE idea_type (
  id             int(11) NOT NULL AUTO_INCREMENT,
  name           varchar(100)  DEFAULT NULL,
  description    varchar(1000)  DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY idea_type_uk_name (name)
);

# --- !Downs

DROP TABLE IF EXISTS idea_type;