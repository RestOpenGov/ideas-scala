# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS idea;

CREATE TABLE idea (
  id             int(11)        NOT NULL AUTO_INCREMENT,
  idea_type_id   int(11)        NOT NULL,
  name           varchar(200)   DEFAULT NULL,
  description    varchar(1000)  DEFAULT NULL,
  user_id        int(11)        NOT NULL,
  views          int(11)        NOT NULL,
  created        timestamp      NULL DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY idea_uk_name (name)
);

ALTER TABLE idea 
ADD FOREIGN KEY (idea_type_id) 
REFERENCES idea_type(id);

ALTER TABLE idea 
ADD FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

DROP TABLE IF EXISTS idea;