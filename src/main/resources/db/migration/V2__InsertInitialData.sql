INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists)
VALUES ('Pep Guardiola', 'COACH', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$eWPeFb3ZniT9/8ilvtHdiehQxwfyRfZ4G3BDVNz6uPSeqfWVaMh2e', 'pep@manchestercity.com', 53, 1.80, 75.0, NULL, 0, 0, 0, 0);

INSERT INTO teams (name, club_name, coach_id, is_lineup_visible)
SELECT 'Manchester City', 'Manchester City', id, false
FROM users
WHERE name = 'Pep Guardiola'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists)
VALUES ('Erling Haaland', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'haaland@manchestercity.com', 23, 1.95, 88.0, NULL, 38, 34, 36, 8);

INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists)
VALUES ('Kevin De Bruyne', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'debruyne@manchestercity.com', 32, 1.81, 70.0, NULL, 31, 29, 8, 18);

UPDATE users
SET team_id = (
    SELECT id
    FROM teams
    WHERE name = 'Manchester City'
    LIMIT 1
)
WHERE name IN ('Erling Haaland', 'Kevin De Bruyne');
