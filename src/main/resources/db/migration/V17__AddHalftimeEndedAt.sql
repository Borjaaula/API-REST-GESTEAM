-- Timestamp de cuando el entrenador inicia la 2ª parte (control manual)
ALTER TABLE matches ADD COLUMN IF NOT EXISTS halftime_ended_at BIGINT DEFAULT NULL;
