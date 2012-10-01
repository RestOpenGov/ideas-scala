# --- First database schema

# --- !Ups

INSERT INTO tag VALUES
  (1, 'tecnologia',     'Informatica, tecnologia y nuevas tendencias',      '2012-09-18 10:45:00'),
  (2, 'internet',       'Sitios web',                                       '2012-09-19 15:25:00'),
  (3, 'bicisendas',     'Senderos de la ciudad para circular en bicicleta', '2012-09-20 18:42:00'),
  (4, 'transporte',     'Medios de transporte y circulacion por la ciudad', '2012-09-20 14:54:00'),
  (5, 'multas',         'Infracciones, multas y demas normativas',          '2012-09-21 03:23:00'),
  (6, 'servicios',      'Servicios que brinda el gobiernos de la ciudad',   '2012-09-22 16:28:00'),
  (7, 'tramites',       'Tramites',                                         '2012-09-23 15:19:00')
;

# --- !Downs

delete from tag where id > 0;