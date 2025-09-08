
CREATE TABLE IF NOT EXISTS "user" (
    "id" UUID PRIMARY KEY,
    "fullname" VARCHAR(255) NOT NULL,
    "username" VARCHAR(100) UNIQUE NOT NULL,
    "avg_opening_elo" INT DEFAULT 0,
    "avg_midgame_elo" INT DEFAULT 0,
    "avg_endgame_elo" INT DEFAULT 0,
    "games_count" INT DEFAULT 0
);
