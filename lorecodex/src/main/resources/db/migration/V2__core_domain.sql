-- Games core tables
CREATE TABLE IF NOT EXISTS games (
    id SERIAL PRIMARY KEY,
    title TEXT,
    description TEXT,
    cover_image TEXT,
    release_date DATE,
    release_year INT,
    release_date_unknown BOOLEAN,
    rating DOUBLE PRECISION,
    likes INT,
    created_at TIMESTAMP NOT NULL,
    igdb_id BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS game_genres (
    game_id BIGINT NOT NULL,
    genre TEXT NOT NULL,
    CONSTRAINT fk_game_genres_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS game_devs_and_publishers (
    game_id BIGINT NOT NULL,
    developer_and_publisher TEXT NOT NULL,
    CONSTRAINT fk_game_devs_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS game_tags (
    game_id BIGINT NOT NULL,
    tag TEXT NOT NULL,
    CONSTRAINT fk_game_tags_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

-- Guides
CREATE TABLE IF NOT EXISTS guides (
    id SERIAL PRIMARY KEY,
    title TEXT,
    content TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_guides_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_guides_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS guide_images (
    id SERIAL PRIMARY KEY,
    url TEXT,
    guide_id BIGINT,
    CONSTRAINT fk_guide_images_guide FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE CASCADE
);

-- Reviews
CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    content TEXT,
    created_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_reviews_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Comments
CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    content TEXT,
    created_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT,
    review_id BIGINT,
    guide_id BIGINT,
    CONSTRAINT fk_comments_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_guide FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE CASCADE
);

-- User Ratings
CREATE TABLE IF NOT EXISTS user_ratings (
    id SERIAL PRIMARY KEY,
    rating INT,
    user_id BIGINT,
    game_id BIGINT,
    CONSTRAINT fk_user_ratings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_ratings_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

-- Follows
CREATE TABLE IF NOT EXISTS follows (
    id SERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT,
    created_at TIMESTAMP,
    read BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Game Notes
CREATE TABLE IF NOT EXISTS game_notes (
    id SERIAL PRIMARY KEY,
    note TEXT,
    created_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_game_notes_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    CONSTRAINT fk_game_notes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

