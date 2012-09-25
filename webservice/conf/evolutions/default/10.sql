# --- First database schema

# --- !Ups

INSERT INTO vote VALUES
  (01, 'idea', 1, null, 1, true,   '2012-09-20 19:20:00'),
  (02, 'idea', 1, null, 2, true,   '2012-09-21 10:12:00'),
  (03, 'idea', 1, null, 3, true,   '2012-09-21 11:14:00'),
  (04, 'idea', 1, null, 4, false,  '2012-09-22 16:45:00'),
  (05, 'idea', 2, null, 2, true,   '2012-09-22 18:34:00'),
  (06, 'idea', 2, null, 3, true,   '2012-09-22 18:34:00'),
  (07, 'idea', 3, null, 1, true,   '2012-09-23 08:56:00'),
  (08, 'idea', 4, null, 1, true,   '2012-09-23 08:56:00'),
  (09, 'idea', 4, null, 2, false,  '2012-09-23 08:56:00'),
  (10, 'idea', 4, null, 3, false,  '2012-09-23 08:56:00'),

  (11, 'comment', null, 1, 1, true,  '2012-09-20 07:03:00'),
  (12, 'comment', null, 1, 2, true,  '2012-09-20 11:34:00'),
  (13, 'comment', null, 2, 4, true,  '2012-09-22 13:43:00'),
  (14, 'comment', null, 2, 3, false, '2012-09-23 12:56:00'),
  (15, 'comment', null, 2, 1, false, '2012-09-24 13:02:00')
;

# --- !Downs

delete from vote;