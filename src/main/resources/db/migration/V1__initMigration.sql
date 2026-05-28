CREATE TABLE teams
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    name              VARCHAR(255)          NULL,
    club_name         VARCHAR(255)          NULL,
    coach_id          BIGINT                NULL,
    is_lineup_visible BIT(1)                NOT NULL,
    CONSTRAINT pk_teams PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    name              VARCHAR(255)          NULL,
    type              VARCHAR(255)          NULL,
    team_name         VARCHAR(255)          NULL,
    club_name         VARCHAR(255)          NULL,
    status            VARCHAR(255)          NULL,
    password          VARCHAR(255)          NULL,
    email             VARCHAR(255)          NULL,
    age               INT                   NOT NULL,
    height            FLOAT                 NOT NULL,
    weight            FLOAT                 NOT NULL,
    profile_image_uri VARCHAR(255)          NULL,
    matches_played    INT                   NOT NULL,
    matches_started   INT                   NOT NULL,
    goals             INT                   NOT NULL,
    assists           INT                   NOT NULL,
    phone             VARCHAR(255)          NULL,
    lineup_x          FLOAT                 NULL,
    lineup_y          FLOAT                 NULL,
    team_id           BIGINT                NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE teams
    ADD CONSTRAINT FK_TEAMS_ON_COACH FOREIGN KEY (coach_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_TEAM FOREIGN KEY (team_id) REFERENCES teams (id);