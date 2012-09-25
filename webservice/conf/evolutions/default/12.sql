# --- First database schema

# --- !Ups

INSERT INTO comment VALUES
  (1, 'Excelente idea, escuche que en mar del plata y santa fe lo estan por implementar', 0, 0, 1, '2012-09-22 15:10:00'),
  (2, 'Podrian empezar dando wifi en lugares publicos, y ver como funciona', 10, 3, 2, '2012-09-22 15:15:00'),
  (3, 'Pasa que en una plaza te robarian la laptop :-(', 3, 4, 3, '2012-09-22 18:15:00'),
  (4, 'Muy bien dicho, hoy casi me pisa un colectivo que se habia metido en la bicisenda', 15, 2, 2, '2012-09-24 15:40:00'),
  (5, 'Primero tenes que sacar turno, segui este link http://apps.buenosaires.gov.ar/apps/turnos_online/paso1.php', 10, 2, 1, '2012-09-24 19:20:00')
;

# --- !Downs

delete from comment;