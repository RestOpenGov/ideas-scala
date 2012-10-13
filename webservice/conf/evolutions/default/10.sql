# --- First database schema

# --- !Ups

INSERT INTO comment VALUES
  (1, 1, 1, 'Excelente idea, escuche que en mar del plata y santa fe lo estan por implementar', '2012-09-22 15:10:00'),
  (2, 1, 3, 'Podrian empezar dando wifi en lugares publicos, y ver como funciona', '2012-09-22 15:15:00'),
  (3, 1, 4, 'Pasa que en una plaza te robarian la laptop :-(', '2012-09-22 18:15:00'),
  (4, 2, 4, 'Muy bien dicho, hoy casi me pisa un colectivo que se habia metido en la bicisenda', '2012-09-24 15:40:00'),
  (5, 4, 1, 'Primero tenes que sacar turno, segui este link http://apps.buenosaires.gov.ar/apps/turnos_online/paso1.php', '2012-09-24 19:20:00')
;

# --- !Downs

delete from comment where id > 0;