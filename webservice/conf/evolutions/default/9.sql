# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS vote;

CREATE TABLE vote (
  id              int(11)        NOT NULL AUTO_INCREMENT,
  vote_type       varchar(10)    NOT NULL,          -- idea | comment
  idea_id         int(11)        NULL,              -- should be null if vote.vote_type = comment
  comment_id      int(11)        NULL,              -- should be null if vote.vote_type = idea
  user_id         int(11)        NOT NULL,
  pos             boolean        NOT NULL,          -- positive = true, negative = false
  created         timestamp      NULL DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX vote_ix_idea     ON vote(idea_id, pos);
CREATE INDEX vote_ix_comment  ON vote(comment_id, pos);
CREATE INDEX vote_ix_user     ON vote(user_id);

ALTER TABLE vote 
ADD CONSTRAINT vote_fk_idea
FOREIGN KEY (idea_id) 
REFERENCES idea(id);

ALTER TABLE vote 
ADD CONSTRAINT vote_fk_comment
FOREIGN KEY (comment_id) 
REFERENCES comment(id);

ALTER TABLE vote 
ADD CONSTRAINT vote_fk_user
FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

ALTER TABLE vote DROP CONSTRAINT vote_fk_idea;
ALTER TABLE vote DROP CONSTRAINT vote_fk_comment;
ALTER TABLE vote DROP CONSTRAINT vote_fk_user;

DROP TABLE IF EXISTS vote;