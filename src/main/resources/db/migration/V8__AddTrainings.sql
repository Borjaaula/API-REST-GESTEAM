CREATE TABLE IF NOT EXISTS trainings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(255),
    date VARCHAR(20),
    time VARCHAR(10),
    notes TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS training_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    training_id BIGINT,
    player_name VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ABSENT',
    INDEX idx_training_id (training_id),
    INDEX idx_player_training (training_id, player_name)
);
