-- Align schema with current JPA entities

-- Ensure baseline tables exist for older deployments or manual drops
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    email_notifications_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

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
    genre TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS game_devs_and_publishers (
    game_id BIGINT NOT NULL,
    developer_and_publisher TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS game_tags (
    game_id BIGINT NOT NULL,
    tag TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS guides (
    id SERIAL PRIMARY KEY,
    title TEXT,
    content TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT
);

CREATE TABLE IF NOT EXISTS guide_images (
    id SERIAL PRIMARY KEY,
    url TEXT,
    guide_id BIGINT
);

CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    content TEXT,
    created_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT
);

CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    content TEXT,
    created_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT,
    review_id BIGINT,
    guide_id BIGINT
);

CREATE TABLE IF NOT EXISTS user_ratings (
    id SERIAL PRIMARY KEY,
    rating INT,
    user_id BIGINT,
    game_id BIGINT
);

CREATE TABLE IF NOT EXISTS follows (
    id SERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT,
    created_at TIMESTAMP,
    read BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS game_notes (
    id SERIAL PRIMARY KEY,
    note TEXT,
    created_at TIMESTAMP,
    game_id BIGINT,
    user_id BIGINT
);

CREATE TABLE IF NOT EXISTS challenge (
    id SERIAL PRIMARY KEY,
    title TEXT,
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP,
    user_id BIGINT
);

CREATE TABLE IF NOT EXISTS challenge_items (
    id SERIAL PRIMARY KEY,
    challenge_id BIGINT NOT NULL,
    game_id BIGINT,
    item_description TEXT,
    completed BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS challenge_participation (
    id SERIAL PRIMARY KEY,
    challenge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS challenge_participation_items (
    id SERIAL PRIMARY KEY,
    participation_id BIGINT NOT NULL,
    challenge_item_id BIGINT NOT NULL,
    completed BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS user_lists (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name TEXT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS list_items (
    id SERIAL PRIMARY KEY,
    list_id BIGINT NOT NULL,
    game_id BIGINT,
    item_type TEXT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS news (
    id SERIAL PRIMARY KEY,
    title TEXT,
    content TEXT,
    created_at TIMESTAMP,
    author_id BIGINT
);

CREATE TABLE IF NOT EXISTS news_images (
    id SERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL,
    url TEXT
);

-- Drop existing foreign keys to allow renames/type changes
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_user;
ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_role;
ALTER TABLE game_genres DROP CONSTRAINT IF EXISTS fk_game_genres_game;
ALTER TABLE game_devs_and_publishers DROP CONSTRAINT IF EXISTS fk_game_devs_game;
ALTER TABLE game_tags DROP CONSTRAINT IF EXISTS fk_game_tags_game;
ALTER TABLE guides DROP CONSTRAINT IF EXISTS fk_guides_game;
ALTER TABLE guides DROP CONSTRAINT IF EXISTS fk_guides_user;
ALTER TABLE guide_images DROP CONSTRAINT IF EXISTS fk_guide_images_guide;
ALTER TABLE reviews DROP CONSTRAINT IF EXISTS fk_reviews_game;
ALTER TABLE reviews DROP CONSTRAINT IF EXISTS fk_reviews_user;
ALTER TABLE comments DROP CONSTRAINT IF EXISTS fk_comments_game;
ALTER TABLE comments DROP CONSTRAINT IF EXISTS fk_comments_user;
ALTER TABLE comments DROP CONSTRAINT IF EXISTS fk_comments_review;
ALTER TABLE comments DROP CONSTRAINT IF EXISTS fk_comments_guide;
ALTER TABLE user_ratings DROP CONSTRAINT IF EXISTS fk_user_ratings_user;
ALTER TABLE user_ratings DROP CONSTRAINT IF EXISTS fk_user_ratings_game;
ALTER TABLE follows DROP CONSTRAINT IF EXISTS fk_follows_follower;
ALTER TABLE follows DROP CONSTRAINT IF EXISTS fk_follows_following;
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS fk_notifications_user;
ALTER TABLE game_notes DROP CONSTRAINT IF EXISTS fk_game_notes_game;
ALTER TABLE game_notes DROP CONSTRAINT IF EXISTS fk_game_notes_user;
ALTER TABLE challenge DROP CONSTRAINT IF EXISTS fk_challenge_user;
ALTER TABLE challenge_items DROP CONSTRAINT IF EXISTS fk_challenge_items_challenge;
ALTER TABLE challenge_items DROP CONSTRAINT IF EXISTS fk_challenge_items_game;
ALTER TABLE challenge_participation DROP CONSTRAINT IF EXISTS fk_challenge_participation_challenge;
ALTER TABLE challenge_participation DROP CONSTRAINT IF EXISTS fk_challenge_participation_user;
ALTER TABLE challenge_participation_items DROP CONSTRAINT IF EXISTS fk_cpi_participation;
ALTER TABLE challenge_participation_items DROP CONSTRAINT IF EXISTS fk_cpi_item;
ALTER TABLE user_lists DROP CONSTRAINT IF EXISTS fk_user_lists_user;
ALTER TABLE list_items DROP CONSTRAINT IF EXISTS fk_list_items_list;
ALTER TABLE list_items DROP CONSTRAINT IF EXISTS fk_list_items_game;
ALTER TABLE news DROP CONSTRAINT IF EXISTS fk_news_author;
ALTER TABLE news_images DROP CONSTRAINT IF EXISTS fk_news_images_news;

-- Upgrade primary key columns to BIGINT
ALTER TABLE roles ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE users ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE games ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE guides ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE guide_images ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE reviews ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE comments ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE user_ratings ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE follows ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE notifications ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE game_notes ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE challenge ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE challenge_items ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE challenge_participation ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE challenge_participation_items ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE user_lists ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE list_items ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE news ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE news_images ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

-- Rename columns to match entity mappings
ALTER TABLE challenge RENAME COLUMN user_id TO creator_id;
ALTER TABLE challenge_items RENAME COLUMN item_description TO description;
ALTER TABLE challenge_participation_items RENAME COLUMN challenge_item_id TO item_id;
ALTER TABLE user_lists RENAME COLUMN name TO title;
ALTER TABLE list_items RENAME COLUMN list_id TO user_list_id;
ALTER TABLE list_items RENAME COLUMN item_type TO type;
ALTER TABLE list_items RENAME COLUMN game_id TO reference_id;
ALTER TABLE news RENAME COLUMN author_id TO user_id;
ALTER TABLE news_images RENAME COLUMN url TO image_url;
ALTER TABLE guide_images RENAME COLUMN url TO image_url;
ALTER TABLE notifications RENAME COLUMN user_id TO recipient_id;
ALTER TABLE game_notes RENAME COLUMN note TO content;

-- Add missing columns for entities
ALTER TABLE challenge ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
ALTER TABLE challenge_items ADD COLUMN IF NOT EXISTS order_position INT;
ALTER TABLE challenge_participation ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;
ALTER TABLE challenge_participation ADD COLUMN IF NOT EXISTS is_completed BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE user_lists ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE user_lists ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE list_items ADD COLUMN IF NOT EXISTS position INT NOT NULL DEFAULT 0;

ALTER TABLE guides ADD COLUMN IF NOT EXISTS cover_image_url TEXT;
ALTER TABLE guides ADD COLUMN IF NOT EXISTS is_published BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE guides ADD COLUMN IF NOT EXISTS is_draft BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE guide_images ADD COLUMN IF NOT EXISTS caption TEXT;

ALTER TABLE reviews ADD COLUMN IF NOT EXISTS rating DOUBLE PRECISION NOT NULL DEFAULT 0;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS likes INT;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS dislikes INT;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE comments ADD COLUMN IF NOT EXISTS news_id BIGINT;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS user_list_id BIGINT;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS challenge_id BIGINT;
ALTER TABLE comments ADD COLUMN IF NOT EXISTS parent_id BIGINT;

ALTER TABLE user_ratings ALTER COLUMN rating TYPE DOUBLE PRECISION USING rating::DOUBLE PRECISION;

ALTER TABLE news ADD COLUMN IF NOT EXISTS summary TEXT;
ALTER TABLE news ADD COLUMN IF NOT EXISTS cover_image_url TEXT;
ALTER TABLE news ADD COLUMN IF NOT EXISTS is_published BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE news ADD COLUMN IF NOT EXISTS is_draft BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE news ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

ALTER TABLE news_images ADD COLUMN IF NOT EXISTS caption TEXT;

-- Enforce non-null columns required by entities
UPDATE users SET email_notifications_enabled = TRUE WHERE email_notifications_enabled IS NULL;
ALTER TABLE users ALTER COLUMN email_notifications_enabled SET NOT NULL;

ALTER TABLE reviews ALTER COLUMN content SET NOT NULL;
ALTER TABLE reviews ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE reviews ALTER COLUMN game_id SET NOT NULL;

UPDATE comments SET content = '' WHERE content IS NULL;
ALTER TABLE comments ALTER COLUMN content SET NOT NULL;
ALTER TABLE comments ALTER COLUMN user_id SET NOT NULL;

UPDATE user_ratings SET rating = 0 WHERE rating IS NULL;
ALTER TABLE user_ratings ALTER COLUMN rating SET NOT NULL;
ALTER TABLE user_ratings ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE user_ratings ALTER COLUMN game_id SET NOT NULL;

UPDATE notifications SET read = FALSE WHERE read IS NULL;
ALTER TABLE notifications ALTER COLUMN read SET NOT NULL;

ALTER TABLE game_notes ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE game_notes ALTER COLUMN game_id SET NOT NULL;

-- Recreate foreign keys with updated column names
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_roles
    ADD CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;

ALTER TABLE game_genres
    ADD CONSTRAINT fk_game_genres_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;
ALTER TABLE game_devs_and_publishers
    ADD CONSTRAINT fk_game_devs_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;
ALTER TABLE game_tags
    ADD CONSTRAINT fk_game_tags_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;

ALTER TABLE guides
    ADD CONSTRAINT fk_guides_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;
ALTER TABLE guides
    ADD CONSTRAINT fk_guides_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE guide_images
    ADD CONSTRAINT fk_guide_images_guide FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE CASCADE;

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_guide FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_user_list FOREIGN KEY (user_list_id) REFERENCES user_lists(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE;

ALTER TABLE user_ratings
    ADD CONSTRAINT fk_user_ratings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_ratings
    ADD CONSTRAINT fk_user_ratings_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;

ALTER TABLE follows
    ADD CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE follows
    ADD CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE game_notes
    ADD CONSTRAINT fk_game_notes_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE;
ALTER TABLE game_notes
    ADD CONSTRAINT fk_game_notes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE challenge
    ADD CONSTRAINT fk_challenge_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE challenge_items
    ADD CONSTRAINT fk_challenge_items_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE;
ALTER TABLE challenge_items
    ADD CONSTRAINT fk_challenge_items_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE SET NULL;

ALTER TABLE challenge_participation
    ADD CONSTRAINT fk_challenge_participation_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id) ON DELETE CASCADE;
ALTER TABLE challenge_participation
    ADD CONSTRAINT fk_challenge_participation_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE challenge_participation_items
    ADD CONSTRAINT fk_cpi_participation FOREIGN KEY (participation_id) REFERENCES challenge_participation(id) ON DELETE CASCADE;
ALTER TABLE challenge_participation_items
    ADD CONSTRAINT fk_cpi_item FOREIGN KEY (item_id) REFERENCES challenge_items(id) ON DELETE CASCADE;

ALTER TABLE user_lists
    ADD CONSTRAINT fk_user_lists_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE list_items
    ADD CONSTRAINT fk_list_items_list FOREIGN KEY (user_list_id) REFERENCES user_lists(id) ON DELETE CASCADE;

ALTER TABLE news
    ADD CONSTRAINT fk_news_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE news_images
    ADD CONSTRAINT fk_news_images_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE;

-- Add missing join tables for tags/likes
CREATE TABLE IF NOT EXISTS guide_tags (
    guide_id BIGINT NOT NULL,
    tag TEXT,
    CONSTRAINT fk_guide_tags_guide FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS guide_likes (
    guide_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (guide_id, user_id),
    CONSTRAINT fk_guide_likes_guide FOREIGN KEY (guide_id) REFERENCES guides(id) ON DELETE CASCADE,
    CONSTRAINT fk_guide_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS news_tags (
    news_id BIGINT NOT NULL,
    tag TEXT,
    CONSTRAINT fk_news_tags_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS news_likes (
    news_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (news_id, user_id),
    CONSTRAINT fk_news_likes_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    CONSTRAINT fk_news_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
