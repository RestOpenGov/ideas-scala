# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS idea_geo;

CREATE TABLE idea_geo (
  id        int(11)       NOT NULL AUTO_INCREMENT,
  idea_id   int(11)       NOT NULL,
  name      varchar(100)  NOT NULL DEFAULT '',
  lat       double        NULL,
  lng       double        NULL,

  PRIMARY KEY (id)
);

CREATE INDEX idea_geo_ix_idea ON idea_geo(idea_id, name);

ALTER TABLE idea_geo 
ADD CONSTRAINT idea_geo_fk_idea
FOREIGN KEY (idea_id) 
REFERENCES idea(id);

# --- !Downs

ALTER TABLE idea_geo DROP CONSTRAINT idea_geo_fk_idea;

DROP TABLE IF EXISTS idea_geo;