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
  created        timestamp      DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY idea_uk_name (name),
);

CREATE INDEX idea_ix_type ON idea(idea_type_id);
CREATE INDEX idea_ix_user ON idea(user_id);
CREATE INDEX idea_ix_views ON idea(views);
CREATE INDEX idea_ix_created ON idea(created);

ALTER TABLE idea 
ADD FOREIGN KEY (idea_type_id) 
REFERENCES idea_type(id);

ALTER TABLE idea 
ADD FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

DROP TABLE IF EXISTS idea;