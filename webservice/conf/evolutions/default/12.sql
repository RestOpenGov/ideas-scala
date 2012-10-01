# --- First database schema

# --- !Ups

INSERT INTO tag VALUES
  (1, 'tecnologia',           'Informatica, tecnologia y nuevas tendencias'),
  (2, 'internet',             'Sitios web'),
  (3, 'bicisendas',           'Senderos de la ciudad para circular en bicicleta'),
  (4, 'transporte',           'Medios de transporte y circulacion por la ciudad'),
  (5, 'multas',               'Infracciones, multas y demas normativas'),
  (6, 'servicios',            'Servicios que brinda el gobiernos de la ciudad'),
  (7, 'tramites',             'Tramites'),
;

# --- !Downs

delete from tag where id > 0;