-- V19: Garantizar que todos los usuarios con team_id FK están también en team_members.
-- Esto repara inconsistencias para jugadores creados directamente por SQL que no
-- estuvieran en team_members (por ejemplo, si V15 se ejecutó antes de que existieran).
INSERT IGNORE INTO team_members (team_id, user_id)
SELECT u.team_id, u.id
FROM users u
WHERE u.team_id IS NOT NULL;
