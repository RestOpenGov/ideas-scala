# --- First database schema

# --- !Ups

INSERT INTO subject VALUES
  (1,   'Arte y cultura',     'Museos, teatros, cines, festivales'),
  (2,   'Medioambiente',      'Protección y cuidado del medioambiente y la ecología'),
  (3,   'Gobierno',           'Actos de gobierno y trámites'),
  (4,   'Tecnología',         'Gobierno electrónico'),
  (5,   'Política',           'Leyes, resoluciones, campañas políticas'),
  (6,   'Economía',           'Subisidios, fomentos, impuestos'),
  (7,   'Transporte',         'Normas de tránsito, infracciones, bicisendas'),
  (8,   'Infraestrucura',     'Edificios, licitaciones, autopistas, construcciones'),
  (9,   'Salud',              'Hostpitales, centros de atención, campañas'),
  (10,  'Educación',          'Colegios, escuelas, campañas educativas'),
  (11,  'Espacio público',    'Parques, paseos, edificios públicos, vía urbana')
;

# --- !Downs

delete from subject where id > 0;