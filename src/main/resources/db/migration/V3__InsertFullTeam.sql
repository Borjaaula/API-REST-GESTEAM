INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists, team_id)
SELECT 'Ederson', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'ederson@manchestercity.com', 30, 1.88, 86.0, NULL, 36, 36, 0, 0, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists, team_id)
SELECT 'Kyle Walker', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'walker@manchestercity.com', 33, 1.83, 83.0, NULL, 34, 34, 1, 4, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists, team_id)
SELECT 'Ruben Dias', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'dias@manchestercity.com', 26, 1.87, 82.0, NULL, 33, 33, 2, 1, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists, team_id)
SELECT 'John Stones', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'stones@manchestercity.com', 29, 1.88, 82.0, NULL, 28, 27, 2, 2, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, age, height, weight, profile_image_uri, matches_played, matches_started, goals, assists, team_id)
SELECT 'Rodri', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'rodri@manchestercity.com', 27, 1.91, 82.0, NULL, 35, 35, 8, 7, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, profile_image_uri, age, height, weight, matches_played, matches_started, goals, assists, team_id)
SELECT 'Bernardo Silva', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'bernardo@manchestercity.com', NULL, 29, 1.73, 64.0, 37, 35, 6, 8, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, profile_image_uri, age, height, weight, matches_played, matches_started, goals, assists, team_id)
SELECT 'Phil Foden', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'foden@manchestercity.com', NULL, 23, 1.71, 70.0, 34, 26, 10, 7, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, profile_image_uri, age, height, weight, matches_played, matches_started, goals, assists, team_id)
SELECT 'Jack Grealish', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'grealish@manchestercity.com', NULL, 28, 1.75, 77.0, 32, 22, 3, 5, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, profile_image_uri, age, height, weight, matches_played, matches_started, goals, assists, team_id)
SELECT 'Julián Álvarez', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'julian@manchestercity.com', NULL, 24, 1.70, 71.0, 39, 18, 15, 9, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, profile_image_uri, age, height, weight, matches_played, matches_started, goals, assists, team_id)
SELECT 'Jeremy Doku', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'doku@manchestercity.com', NULL, 21, 1.71, 68.0, 31, 12, 5, 6, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;

INSERT INTO users (name, type, team_name, club_name, status, password, email, profile_image_uri, age, height, weight, matches_played, matches_started, goals, assists, team_id)
SELECT 'Oscar Bobb', 'PLAYER', 'Manchester City', 'Manchester City', 'APPROVED', '$2a$10$dg49t0A4WDXG20RoU5bSGe.kmFnyj5UQBDtTwjs42HIRtOc3ZC3oi', 'bobb@manchestercity.com', NULL, 20, 1.73, 68.0, 18, 5, 2, 3, t.id
FROM teams t
WHERE t.name = 'Manchester City'
LIMIT 1;
