# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS subscription;

CREATE TABLE subscription (
  id        int(11)   NOT NULL AUTO_INCREMENT,
  idea_id   int(11)   NOT NULL,
  user_id   int(11)   NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY subscription_uk (idea_id, user_id)
);

CREATE INDEX subscription_ix_idea ON subscription(idea_id, user_id);
CREATE INDEX subscription_ix_user ON subscription(user_id, idea_id);

ALTER TABLE subscription 
ADD CONSTRAINT subscription_fk_idea
FOREIGN KEY (idea_id) 
REFERENCES idea(id);

ALTER TABLE subscription 
ADD CONSTRAINT subscription_fk_user
FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

ALTER TABLE subscription DROP CONSTRAINT subscription_fk_idea;
ALTER TABLE subscription DROP CONSTRAINT subscription_fk_user;

DROP TABLE IF EXISTS subscription;