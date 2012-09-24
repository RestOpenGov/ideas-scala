# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS comment;

CREATE TABLE comment (
  id             int(11)        NOT NULL AUTO_INCREMENT,
  comment        TEXT           NOT NULL,
  positiveVote   int(11)        DEFAULT NULL,
  negativeVote   int(11)        DEFAULT NULL,
  author         int(11)        NOT NULL,
  created        timestamp      DEFAULT NULL,

  PRIMARY KEY (id),
);



ALTER TABLE comment 
ADD FOREIGN KEY (author) 
REFERENCES user(id);

# --- !Downs

DROP TABLE IF EXISTS comment;