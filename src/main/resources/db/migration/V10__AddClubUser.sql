-- V10: Añadir usuario CLUB de demostración "Manchester City"
INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, matches_played, matches_started, goals, assists)
VALUES (
    'Manchester City',
    'CLUB',
    NULL,
    'Manchester City',
    'APPROVED',
    '$2a$10$dZcctpXeSSVYINIEOLl.Q.vN6ssPqs1qJt/UchFJRm8xLFS.Rb1RC',
    'admin@manchestercity.com',
    0, 0, 0, 0, 0, 0, 0
);
