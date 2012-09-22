# --- First database schema

# --- !Ups

INSERT INTO `user` VALUES
  (1, 'nardoz',     'Mister Nardoz',      'nardoz@hotmail.com',   'nardoz.avatar.png',      '2012-09-23 11:45:00'),
  (2, 'opensas',    'Sebastian Scarano',  'scarano@hotmail.com',  'opensas.avatar.png',     '2012-09-22 10:40:00'),
  (3, 'darkipunch', 'Mister dark punch',  'darki@hotmail.com',    'darkipunch.avatar.png',  '2012-09-24 18:50:00'),
  (4, 'pala',       'Mister paladin',     'pala@hotmail.com',     'pala.avatar.png',        '2012-09-25 21:30:00')
;

# --- !Downs

delete from `user`;