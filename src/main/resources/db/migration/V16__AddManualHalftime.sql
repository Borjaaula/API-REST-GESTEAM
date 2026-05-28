-- Añade la columna para controlar el partido manualmente (descanso manual)
ALTER TABLE matches ADD COLUMN IF NOT EXISTS manual_halftime BOOLEAN NOT NULL DEFAULT FALSE;
