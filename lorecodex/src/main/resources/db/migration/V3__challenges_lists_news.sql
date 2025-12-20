-- Challenges
CREATE TABLE IF NOT EXISTS challenge (
    id SERIAL PRIMARY KEY,
    title TEXT,
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP,
    user_id BIGINT,
    CONSTRAINT fk_challenge_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS challenge_items (
    id SERIAL PRIMARY KEY,
    challenge_id BIGINT NOT NULL,
    game_id BIGINT,
    item_description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_challenge_items_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE,
    CONSTRAINT fk_challenge_items_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS challenge_participation (
    id SERIAL PRIMARY KEY,
    challenge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP,
    CONSTRAINT fk_challenge_participation_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE,
    CONSTRAINT fk_challenge_participation_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS challenge_participation_items (
    id SERIAL PRIMARY KEY,
    participation_id BIGINT NOT NULL,
    challenge_item_id BIGINT NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_cpi_participation FOREIGN KEY (participation_id) REFERENCES challenge_participation(id) ON DELETE CASCADE,
    CONSTRAINT fk_cpi_item FOREIGN KEY (challenge_item_id) REFERENCES challenge_items(id) ON DELETE CASCADE
);

-- Lists
CREATE TABLE IF NOT EXISTS user_lists (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name TEXT,
    created_at TIMESTAMP,
    CONSTRAINT fk_user_lists_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS list_items (
    id SERIAL PRIMARY KEY,
    list_id BIGINT NOT NULL,
    game_id BIGINT,
    item_type TEXT,
    created_at TIMESTAMP,
    CONSTRAINT fk_list_items_list FOREIGN KEY (list_id) REFERENCES user_lists(id) ON DELETE CASCADE,
    CONSTRAINT fk_list_items_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE SET NULL
);

-- News
CREATE TABLE IF NOT EXISTS news (
    id SERIAL PRIMARY KEY,
    title TEXT,
    content TEXT,
    created_at TIMESTAMP,
    author_id BIGINT,
    CONSTRAINT fk_news_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS news_images (
    id SERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL,
    url TEXT,
    CONSTRAINT fk_news_images_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

