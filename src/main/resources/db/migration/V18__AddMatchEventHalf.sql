-- Añade la columna 'half' a match_events para indicar en qué parte ocurrió el evento (1 o 2)
ALTER TABLE match_events ADD COLUMN IF NOT EXISTS half INT NOT NULL DEFAULT 1;
