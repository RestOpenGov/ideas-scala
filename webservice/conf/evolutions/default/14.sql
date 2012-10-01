# --- First database schema

# --- !Ups

INSERT INTO vote idea_tag
  (01, 1, 1),
  (02, 1, 2),
  (03, 2, 3),
  (04, 2, 4),
  (05, 2, 5),
  (05, 3, 6),
  (07, 4, 4),
  (08, 4, 7)
;

# --- !Downs

delete from idea_tag;