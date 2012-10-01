# --- First database schema

# --- !Ups

DROP TABLE IF EXISTS notification;

CREATE TABLE notification (
  id             int(11) NOT NULL AUTO_INCREMENT,
  user_id        int(11)        NOT NULL,
  state          varchar(100)   NOT NULL,   -- pending | sent
  content        varchar(10000) DEFAULT NULL,
  created        timestamp      NULL DEFAULT NULL,

  PRIMARY KEY (id)
);

CREATE INDEX notification_ix_user ON notification(user_id);

ALTER TABLE notification 
ADD CONSTRAINT notification_fk_user
FOREIGN KEY (user_id) 
REFERENCES user(id);

# --- !Downs

ALTER TABLE notification DROP CONSTRAINT notification_fk_user;

DROP TABLE IF EXISTS notification;