-- name: FindUserByUsername
SELECT
    id,
    full_name,
    username,
    avg_opening_elo,
    avg_middle_elo,
    avg_endgame_elo,
    games_count
FROM user
WHERE username = :username;

-- name: UpdateUserElo
UPDATE user
SET avg_opening_elo = :avg_opening_elo,
    avg_middle_elo  = :avg_middle_elo,
    avg_endgame_elo = :avg_endgame_elo,
    games_count     = :games_count
WHERE id = :id;
