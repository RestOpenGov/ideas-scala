# --- First database schema

# --- !Ups

INSERT INTO idea_type VALUES
  (1, 'idea',       'Una propuesta para mejorar la ciudad'),
  (2, 'reclamo',    ''),
  (3, 'pregunta',   'Una pregunta acerca de la ciudad para que la comunidad responda')
;

# --- !Downs

delete from idea_type;