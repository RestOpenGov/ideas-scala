# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS `identity`;

CREATE TABLE `identity` (
  id                int(11)       NOT NULL AUTO_INCREMENT,
  user_id           int(11)       NOT NULL,
  provider          varchar(20)   NOT NULL,   -- twitter | facebook
  provider_id       varchar(100)  NOT NULL,   -- user's id in twitter | facebook
  created           timestamp     NOT NULL DEFAULT current_timestamp,

  PRIMARY KEY (id),
  UNIQUE KEY identity_uk_user_provider (user_id, provider)

);

ALTER TABLE identity 
ADD CONSTRAINT identity_user
FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

DROP TABLE IF EXISTS `identity`;