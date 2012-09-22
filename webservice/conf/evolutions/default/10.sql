# --- First database schema

# --- !Ups

INSERT INTO vote VALUES
  (1, 'idea', 1, null, 1, true),
  (2, 'idea', 1, null, 2, true),
  (3, 'idea', 1, null, 3, true),
  (4, 'idea', 1, null, 4, false),
  (5, 'idea', 2, null, 2, true),
  (6, 'idea', 3, null, 1, true),
  (7, 'comment', null, 1, 1, true),
  (8, 'comment', null, 1, 2, true),
  (9, 'comment', null, 2, 4, true),
  (10, 'comment', null, 2, 3, false),
  (11, 'comment', null, 2, 1, false)
;

# --- !Downs

delete from vote;