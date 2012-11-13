# --- First database schema

# --- !Ups

INSERT INTO tag VALUES
  (1, 'tecnologia',     'Informática, tecnología y nuevas tendencias',      '2012-09-18 10:45:00'),
  (2, 'internet',       'Sitios web',                                       '2012-09-19 15:25:00'),
  (3, 'bicisendas',     'Senderos de la ciudad para circular en bicicleta', '2012-09-20 18:42:00'),
  (4, 'transporte',     'Medios de transporte y circulación por la ciudad', '2012-09-20 14:54:00'),
  (5, 'multas',         'Infracciones, multas y demás normativas',          '2012-09-21 03:23:00'),
  (6, 'servicios',      'Servicios que brinda el gobierno de la ciudad',    '2012-09-22 16:28:00'),
  (7, 'tramites',       'Trámites',                                         '2012-09-23 15:19:00'),
  (8, 'cultura',        'Cultura',                                          '2012-09-23 16:45:00'),
  (9, 'cine',           'Cine',                                             '2012-09-26 18:32:00')
;

# --- !Downs

delete from tag where id > 0;