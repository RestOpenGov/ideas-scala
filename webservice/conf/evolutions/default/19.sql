# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS categorization;

CREATE TABLE categorization (
  id          int(11)       NOT NULL AUTO_INCREMENT,
  idea_id     int(11)       NOT NULL,
  category    varchar(100)  NOT NULL,
  name        varchar(100)  NOT NULL,
  latitude    integer       NULL,
  longitude   integer       NULL,
  created     timestamp     NULL DEFAULT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX categorization_ix_idea ON categorization(idea_id);

ALTER TABLE categorization 
ADD CONSTRAINT categorization_fk_idea
FOREIGN KEY (idea_id) 
REFERENCES idea(id);

# --- !Downs

ALTER TABLE categorization DROP CONSTRAINT categorization_fk_idea;

DROP TABLE IF EXISTS categorization;