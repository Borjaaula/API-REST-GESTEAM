-- V15: Relación ManyToMany entre equipos y jugadores/entrenadores
CREATE TABLE IF NOT EXISTS team_members (
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (team_id, user_id),
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Migrar datos existentes del FK team_id en users
INSERT IGNORE INTO team_members (team_id, user_id)
SELECT u.team_id, u.id
FROM users u
WHERE u.team_id IS NOT NULL;
