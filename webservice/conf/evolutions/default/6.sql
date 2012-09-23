# --- First database schema

# --- !Ups

INSERT INTO idea VALUES
  (1, 1, 'Wifi libre en ba', 'Proveer acceso wifi gratuito en toda la ciudad', 2, 320, '2012-09-22 14:20:00'),
  (2, 1, 'Multas para los autos que no respetan las bicisendas', 'Implementar multas...', 1, 124, '2012-09-23 15:20:00'),
  (3, 2, 'Cortar las ramas de corrientes y malabia', 'En corrientes y malabia hay ramas de arbol que impiden el paso', 3, 25, '2012-09-24 19:20:00'),
  (4, 3, 'Como hago para sacar el registro de conducir?', 'Que tramite debo hacer para sacar el registro?', 1, 67, '2012-09-24 19:20:00')
;

# --- !Downs

delete from idea;