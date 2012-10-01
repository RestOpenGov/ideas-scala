# --- First database schema

# --- !Ups



# --- !Downs

/*
ALTER TABLE categorization DROP CONSTRAINT categorization_fk_idea;
*/
DROP TABLE IF EXISTS categorization;

/*
ALTER TABLE notification DROP CONSTRAINT notification_fk_user;
*/
DROP TABLE IF EXISTS notification;

/*
ALTER TABLE subscription DROP CONSTRAINT subscription_fk_idea;
ALTER TABLE subscription DROP CONSTRAINT subscription_fk_user;
*/
DROP TABLE IF EXISTS subscription;

/*
ALTER TABLE idea_tag DROP CONSTRAINT idea_tag_fk_idea;
ALTER TABLE idea_tag DROP CONSTRAINT idea_tag_fk_tag;
*/
DROP TABLE IF EXISTS idea_tag;


DROP TABLE IF EXISTS tag;

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