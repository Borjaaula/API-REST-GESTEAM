-- Migración V13: Añadir columnas de posición al jugador
-- position         → posición principal  (PORTERO, DEFENSA, MEDIO, ATACANTE)
-- secondary_position → posición alternativa (opcional)

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS position VARCHAR(20) NULL,
    ADD COLUMN IF NOT EXISTS secondary_position VARCHAR(20) NULL;
