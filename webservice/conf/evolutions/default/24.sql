# --- First database schema

# --- !Ups

INSERT INTO identity VALUES
  (01, 1, 'twitter',    'twitter_id_for_user_1',    '2012-09-24 19:20:00'),
  (02, 1, 'facebook',   'facebook_id_for_user_1',   '2012-09-24 19:20:00'),
  (03, 2, 'twitter',    'twitter_id_for_user_2',    '2012-09-24 19:20:00'),
  (04, 3, 'facebook',   'facebook_id_for_user_3',   '2012-09-24 19:20:00')
;

# --- !Downs

delete from identity where id > 0;