# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  id             int(11) NOT NULL AUTO_INCREMENT,
  nickname       varchar(100)  DEFAULT NULL,
  name           varchar(100)  DEFAULT NULL,
  email          varchar(100)  DEFAULT NULL,
  avatar         varchar(100)  DEFAULT NULL,
  created        timestamp     NULL DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY user_uk_nickname (nickname),
  UNIQUE KEY user_uk_name (name),
  UNIQUE KEY user_uk_email (email)
);

# --- !Downs

DROP TABLE IF EXISTS `user`;