-- 01. Tạo bảng users
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR,
    last_name  VARCHAR,
    email      VARCHAR UNIQUE,
    role       INTEGER,
    username   VARCHAR UNIQUE,
    password   VARCHAR,
    bio        TEXT,
    avatar     VARCHAR,
    facebook   VARCHAR,
    instagram  VARCHAR,
    tiktok     VARCHAR,
    youtube    VARCHAR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 02. Tạo bảng user_followers (mối quan hệ nhiều-nhiều giữa người dùng)
CREATE TABLE user_followers
(
    id_user     INTEGER,
    id_follower INTEGER,
    CHECK (id_user != id_follower
) ,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user, id_follower),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_follower) REFERENCES users(id) ON DELETE CASCADE,
);

-- ENUM for Video's Status
CREATE TYPE video_status AS ENUM ('DRAFT', 'PUBLISHED', 'DELETED', 'BLOCKED');

-- Tạo bảng videos
CREATE TABLE videos
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR,
    category    VARCHAR,
    style       VARCHAR,
    target      VARCHAR,
    script      TEXT,
    audio_url   VARCHAR,
    video_url   VARCHAR,
    like_cnt    INTEGER      DEFAULT 0,
    dislike_cnt INTEGER      DEFAULT 0,
    comment_cnt  INTEGER      DEFAULT 0,
    view_cnt    INTEGER      DEFAULT 0,
    length      NUMERIC,
    thumbnail   VARCHAR,
    status      video_status DEFAULT 'DRAFT',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    id_user     INTEGER,
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE SET NULL
);

-- Tạo bảng liked_videos
CREATE TABLE liked_videos
(
    id_user    INTEGER,
    id_video   INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user, id_video),
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (id_video) REFERENCES videos (id) ON DELETE CASCADE
);

-- Tạo bảng disliked_videos
CREATE TABLE disliked_videos
(
    id_user    INTEGER,
    id_video   INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user, id_video),
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (id_video) REFERENCES videos (id) ON DELETE CASCADE
);

-- Tạo bảng commented_video
CREATE TABLE commented_video
(
    id         SERIAL PRIMARY KEY,
    id_video   INTEGER,
    id_user    INTEGER,
    content    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_video) REFERENCES videos (id) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE
);

-- Tạo bảng video_images
CREATE TABLE video_images
(
    id_video  INTEGER NOT NULL,
    image_url VARCHAR NOT NULL,
    PRIMARY KEY (id_video, image_url),
    FOREIGN KEY (id_video) REFERENCES videos (id) ON DELETE CASCADE
);

-- Tạo bảng video_tags
CREATE TABLE video_tags
(
    id_video INTEGER NOT NULL,
    tag_name VARCHAR NOT NULL,
    PRIMARY KEY (id_video, tag_name),
    FOREIGN KEY (id_video) REFERENCES videos (id) ON DELETE CASCADE
);

-- Trigger cho 3 trường count trong videos
CREATE OR REPLACE FUNCTION update_video_counters()
RETURNS TRIGGER AS $$
BEGIN
    -- Xử lý cho liked_videos
    IF TG_TABLE_NAME = 'liked_videos' THEN
        IF TG_OP = 'INSERT' THEN
            UPDATE videos SET like_cnt = like_cnt + 1 WHERE id = NEW.id_video;
        ELSIF TG_OP = 'DELETE' THEN
            UPDATE videos SET like_cnt = like_cnt - 1 WHERE id = OLD.id_video;
        END IF;

    -- Xử lý cho disliked_videos
    ELSIF TG_TABLE_NAME = 'disliked_videos' THEN
        IF TG_OP = 'INSERT' THEN
            UPDATE videos SET dislike_cnt = dislike_cnt + 1 WHERE id = NEW.id_video;
        ELSIF TG_OP = 'DELETE' THEN
            UPDATE videos SET dislike_cnt = dislike_cnt - 1 WHERE id = OLD.id_video;
        END IF;

    -- Xử lý cho commented_video
    ELSIF TG_TABLE_NAME = 'commented_video' THEN
        IF TG_OP = 'INSERT' THEN
            UPDATE videos SET comment_cnt = comment_cnt + 1 WHERE id = NEW.id_video;
        ELSIF TG_OP = 'DELETE' THEN
            UPDATE videos SET comment_cnt = comment_cnt - 1 WHERE id = OLD.id_video;
        END IF;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_liked_videos_update
AFTER INSERT OR DELETE ON liked_videos
FOR EACH ROW
EXECUTE FUNCTION update_video_counters();

CREATE TRIGGER trg_disliked_videos_update
AFTER INSERT OR DELETE ON disliked_videos
FOR EACH ROW
EXECUTE FUNCTION update_video_counters();

CREATE TRIGGER trg_commented_video_update
AFTER INSERT OR DELETE ON commented_video
FOR EACH ROW
EXECUTE FUNCTION update_video_counters();

INSERT INTO videos (title, category, style, target, script, audio_url, video_url, length, thumbnail, status, id_user)
VALUES (
           'Funny Cat',
           'Comedy',
           'Cartoon',
           'General',
           'Funny cat jumps around the house',
           'https://audio.url/funnycat.mp3',
           'https://video.url/funnycat.mp4',
           2.5,
           'https://thumb.url/cat.png',
           'PUBLISHED',
           1
       );

INSERT INTO liked_videos (id_user, id_video)
VALUES (1, 4);

INSERT INTO commented_video (id_video, id_user, content)
VALUES (4, 1, 'This video is hilarious!');

