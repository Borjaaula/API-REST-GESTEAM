CREATE TABLE IF NOT EXISTS matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(255),
    rival_team VARCHAR(255),
    date VARCHAR(20),
    start_time VARCHAR(10),
    part_duration INT DEFAULT 45,
    break_duration INT DEFAULT 15,
    goals_for INT DEFAULT 0,
    goals_against INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    is_public BOOLEAN DEFAULT FALSE,
    location VARCHAR(255),
    INDEX idx_team_name (team_name),
    INDEX idx_date (date)
);

CREATE TABLE IF NOT EXISTS match_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT,
    type VARCHAR(30),
    player_name VARCHAR(255),
    minute INT,
    assist_player_name VARCHAR(255),
    player_out_name VARCHAR(255),
    player_in_name VARCHAR(255),
    INDEX idx_match_id (match_id)
);
