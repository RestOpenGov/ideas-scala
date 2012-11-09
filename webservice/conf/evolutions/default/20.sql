# --- First database schema

# --- !Ups

INSERT INTO subscription VALUES
  (01, 1, 2),
  (02, 1, 1),
  (03, 1, 3),
  (04, 1, 4),
  (05, 2, 1),
  (06, 2, 2),
  (07, 3, 3),
  (08, 4, 1),
  (09, 1, 5)
;

# --- !Downs

delete from subscription where id > 0;