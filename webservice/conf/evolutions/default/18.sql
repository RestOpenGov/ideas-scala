# --- First database schema

# --- !Ups

INSERT INTO idea_geo VALUES
  (01, 3, 'corrientes y malabia', -34.5929527, -58.4302979),
  (02, 5, 'Cine Gaumont - Rivadavia 1635', -34.609117, -58.3896655),
  (04, 5, 'Cine La Máscara - Piedras 736', -34.6167007, -58.3774124),
  (03, 5, 'Cine Arte Cinema - Salta 1620', -34.6271162, -58.3828335)

;

# --- !Downs

delete from idea_geo where id > 0;