# --- First database schema

# --- !Ups

INSERT INTO notification VALUES
  (01, 1, 'pending', 'notification report for user nardoz',     '2012-09-22 14:20:00'),
  (02, 2, 'pending', 'notification report for user opensas',    '2012-09-22 14:20:02'),
  (03, 3, 'pending', 'notification report for user darkipunch', '2012-09-22 14:20:04'),
  (04, 4, 'pending', 'notification report for user pala',       '2012-09-22 14:20:08')
;

# --- !Downs

delete from notification where id > 0;