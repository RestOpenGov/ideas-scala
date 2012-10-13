# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS idea_tag;

CREATE TABLE idea_tag (
  id        int(11)   NOT NULL AUTO_INCREMENT,
  idea_id   int(11)   NOT NULL,
  tag_id    int(11)   NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY idea_tag_uk (idea_id, tag_id)
);

CREATE INDEX idea_tag_ix_idea ON idea_tag(idea_id, tag_id);
CREATE INDEX idea_tag_ix_tag ON idea_tag(tag_id, idea_id);

ALTER TABLE idea_tag 
ADD CONSTRAINT idea_tag_fk_idea
FOREIGN KEY (idea_id) 
REFERENCES idea(id);

ALTER TABLE idea_tag 
ADD CONSTRAINT idea_tag_fk_tag
FOREIGN KEY (tag_id) 
REFERENCES tag(id);

# --- !Downs

ALTER TABLE idea_tag DROP CONSTRAINT idea_tag_fk_idea;
ALTER TABLE idea_tag DROP CONSTRAINT idea_tag_fk_tag;

DROP TABLE IF EXISTS idea_tag;