# --- First database schema

# --- !Ups

INSERT INTO vote VALUES
  (1, 'idea', 1, null, 1, true,   '2012-09-20 19:20:00'),
  (2, 'idea', 1, null, 2, true,   '2012-09-21 10:12:00'),
  (3, 'idea', 1, null, 3, true,   '2012-09-21 11:14:00'),
  (4, 'idea', 1, null, 4, false,  '2012-09-22 16:45:00'),
  (5, 'idea', 2, null, 2, true,   '2012-09-22 18:34:00'),
  (6, 'idea', 3, null, 1, true,   '2012-09-23 08:56:00'),

  (7,   'comment', null, 1, 1, true,  '2012-09-20 07:03:00'),
  (8,   'comment', null, 1, 2, true,  '2012-09-20 11:34:00'),
  (9,   'comment', null, 2, 4, true,  '2012-09-22 13:43:00'),
  (10,  'comment', null, 2, 3, false, '2012-09-23 12:56:00'),
  (11,  'comment', null, 2, 1, false, '2012-09-24 13:02:00')
;

# --- !Downs

delete from vote;