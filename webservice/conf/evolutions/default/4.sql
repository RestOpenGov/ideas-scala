# --- First database schema

# --- !Ups

INSERT INTO idea_type VALUES
  (1, 'idea',         'Una propuesta para mejorar la ciudad'),
  (2, 'reclamo',      'Algo que no nos gusta de la ciudad y que tenemos que cambiar'),
  (3, 'pregunta',     'Una pregunta acerca de la ciudad para que la comunidad responda'),
  (4, 'sugerencia',   'Algún lugar o evento de la ciudad que no te podés perder')
;

# --- !Downs

delete from idea_type where id > 0;