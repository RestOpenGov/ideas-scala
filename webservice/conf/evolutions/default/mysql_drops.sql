# --- First database schema

# --- !Ups



# --- !Downs

/*
ALTER TABLE vote DROP CONSTRAINT vote_fk_idea; 
ALTER TABLE vote DROP CONSTRAINT vote_fk_comment;
ALTER TABLE vote DROP CONSTRAINT vote_fk_user;
*/
DROP TABLE IF EXISTS vote;

/*
ALTER TABLE comment DROP CONSTRAINT comment_fk_idea;
ALTER TABLE comment DROP CONSTRAINT comment_fk_user;
*/
DROP TABLE IF EXISTS comment;

/*
ALTER TABLE idea DROP CONSTRAINT idea_fk_idea_type;
ALTER TABLE idea DROP CONSTRAINT idea_fk_user;
*/
DROP TABLE IF EXISTS idea;

DROP TABLE IF EXISTS idea_type;

DROP TABLE IF EXISTS `user`;

DROP TABLE IF EXISTS play_evolutions;