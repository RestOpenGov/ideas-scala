# --- First database schema

# --- !Ups

INSERT INTO categorization VALUES
  (01, 3, 'address', 'Corrientes y Malabia', 12354, 45673,      '2012-09-24 19:20:00')
;

# --- !Downs

delete from categorization where id > 0;