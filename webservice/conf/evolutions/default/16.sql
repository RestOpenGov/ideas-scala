# --- First database schema

# --- !Ups

INSERT INTO idea_tag VALUES
  (01, 1, 1),
  (02, 1, 2),
  (03, 2, 3),
  (04, 2, 4),
  (05, 2, 5),
  (06, 3, 6),
  (07, 4, 4),
  (08, 4, 7),
  (09, 5, 8),
  (10, 5, 9)
;

# --- !Downs

delete from idea_tag where id > 0;