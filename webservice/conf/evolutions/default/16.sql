# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS comment;

CREATE TABLE comment (
  id             int(11)        NOT NULL AUTO_INCREMENT,
  idea_id        int(11)        NOT NULL,
  user_id        int(11)        NOT NULL,
  description    varchar(1000)  DEFAULT NULL,
  created        timestamp      DEFAULT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE comment 
ADD FOREIGN KEY (idea_id) 
REFERENCES idea(id);

ALTER TABLE comment 
ADD FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

DROP TABLE IF EXISTS comment;