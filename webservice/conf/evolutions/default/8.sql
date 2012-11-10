# --- First database schema

# --- !Ups

INSERT INTO idea VALUES
  (1, 1, 4,   'Wifi libre en ba', 'Proveer acceso wifi gratuito en toda la ciudad', 2, 320, '2012-09-22 14:20:00'),
  (2, 1, 7,   'Multas para los autos que no respetan las bicisendas', 'Implementar multas...', 1, 124, '2012-09-23 15:20:00'),
  (3, 2, 11,  'Cortar las ramas de corrientes y malabia', 'En corrientes y malabia hay ramas de arbol que impiden el paso', 3, 25, '2012-09-24 19:20:00'),
  (4, 3, 3,   'Como hago para sacar el registro de conducir?', 'Que tramite debo hacer para sacar el registro?', 1, 67, '2012-09-24 19:20:00'),
  (5, 4, 1,   'Ver cine nacional en las salas del espacio INCAA', 'Cine nacional y latinoamericano a precios verdaderamente accesible. También hay descuentos para jubilados, estudiantes y sindicatos.', 1, 43, '2012-09-28 14:10:00')
;

# --- !Downs

delete from idea where id > 0;