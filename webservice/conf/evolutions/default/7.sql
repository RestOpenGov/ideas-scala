# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS comment;

CREATE TABLE comment (
  id        int(11)         NOT NULL AUTO_INCREMENT,
  idea_id   int(11)         NOT NULL,
  user_id   int(11)         NOT NULL,
  comment   varchar(1000)   DEFAULT NULL,
  created   timestamp       NULL DEFAULT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX comment_ix_idea ON comment(idea_id, created);

ALTER TABLE comment 
ADD CONSTRAINT comment_fk_idea
FOREIGN KEY (idea_id) 
REFERENCES idea(id);

ALTER TABLE comment 
ADD CONSTRAINT comment_fk_user
FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

ALTER TABLE comment DROP CONSTRAINT comment_fk_idea;
ALTER TABLE comment DROP CONSTRAINT comment_fk_user;

DROP TABLE IF EXISTS comment;