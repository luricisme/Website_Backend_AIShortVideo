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

-- SET UP TRIGGER FOR COUNT LIKE, DISLIKE AND COMMENT
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

-- ADD DATA FOR TESTING API
INSERT INTO videos (title, category, style, target, script, audio_url, video_url, length, thumbnail, status, id_user, view_cnt)
VALUES
-- Comedy category
('Cat Dance', 'Comedy', 'Live Action', 'General', 'A dancing cat in a living room', 'https://audio.url/catdance.mp3', 'https://video.url/catdance.mp4', 3.0, 'https://thumb.url/catdance.png', 'PUBLISHED', 1, 1500),

-- Education category
('Physics Basics', 'Education', 'Animated', 'Teens', 'Learn Newton’s Laws', 'https://audio.url/physics.mp3', 'https://video.url/physics.mp4', 5.0, 'https://thumb.url/physics.png', 'PUBLISHED', 26, 4200),
('Math Tricks', 'Education', 'Whiteboard', 'Teens', 'Cool math shortcuts', 'https://audio.url/math.mp3', 'https://video.url/math.mp4', 4.0, 'https://thumb.url/math.png', 'PUBLISHED', 7, 3800),

-- Music category
('Lo-fi Chill', 'Music', 'Lo-fi', 'General', 'Relaxing lo-fi beats', 'https://audio.url/lofi.mp3', 'https://video.url/lofi.mp4', 2.5, 'https://thumb.url/lofi.png', 'PUBLISHED', 10, 8000),

-- News category
('Morning Headlines', 'News', 'Talking Head', 'Adults', 'Top news stories of the day', 'https://audio.url/news.mp3', 'https://video.url/news.mp4', 3.5, 'https://thumb.url/news.png', 'PUBLISHED', 11, 600),

-- Entertainment category
('Dance Challenge', 'Entertainment', 'Live', 'Teens', 'New TikTok dance', 'https://audio.url/dance.mp3', 'https://video.url/dance.mp4', 3.2, 'https://thumb.url/dance.png', 'PUBLISHED', 26, 7200),
('Prank Gone Wrong', 'Entertainment', 'Prank', 'Teens', 'Funny prank failed', 'https://audio.url/prank.mp3', 'https://video.url/prank.mp4', 2.8, 'https://thumb.url/prank.png', 'PUBLISHED', 21, 5500);


INSERT INTO video_tags (id_video, tag_name) VALUES (4, 'cat'), (4, 'funny'), (4, 'home');

INSERT INTO video_tags (id_video, tag_name) VALUES (26, 'cat'), (26, 'dance'), (26, 'music');

INSERT INTO video_tags (id_video, tag_name) VALUES (27, 'physics'), (27, 'education'), (27, 'science');

INSERT INTO video_tags (id_video, tag_name) VALUES (28, 'math'), (28, 'tricks'), (28, 'education');

INSERT INTO video_tags (id_video, tag_name) VALUES (29, 'lofi'), (29, 'chill'), (29, 'music');

INSERT INTO video_tags (id_video, tag_name) VALUES (30, 'news'), (30, 'daily'), (30, 'update');

INSERT INTO video_tags (id_video, tag_name) VALUES (31, 'dance'), (31, 'challenge'), (31, 'viral');

INSERT INTO video_tags (id_video, tag_name) VALUES (32, 'prank'), (32, 'funny'), (32, 'fail');


INSERT INTO liked_videos (id_user, id_video)
VALUES (1, 4);

INSERT INTO commented_video (id_video, id_user, content)
VALUES (4, 1, 'This video is hilarious!');

-- Insert test trending month videos
INSERT INTO videos (title, category, style, target, script, audio_url, video_url,
                    like_cnt, dislike_cnt, comment_cnt, view_cnt, length, thumbnail,
                    status, created_at, updated_at, id_user)
VALUES
    ('How to Cook Pasta', 'Food', 'Tutorial', 'Beginner', 'Step by step pasta guide',
     'audio1.mp3', 'video1.mp4', 120, 3, 10, 5000, 3.5, 'thumb1.jpg',
     'PUBLISHED', CURRENT_DATE - INTERVAL '2 days', CURRENT_TIMESTAMP, 1),

    ('Workout Routine', 'Fitness', 'Vlog', 'Everyone', 'A quick workout video',
     'audio2.mp3', 'video2.mp4', 300, 5, 20, 15000, 8.0, 'thumb2.jpg',
     'PUBLISHED', CURRENT_DATE - INTERVAL '10 days', CURRENT_TIMESTAMP, 2),

-- Video PUBLISHED nhưng tháng trước
    ('Old Travel Vlog', 'Travel', 'Vlog', 'Everyone', 'Trip to Da Nang',
     'audio3.mp3', 'video3.mp4', 200, 2, 5, 8000, 6.0, 'thumb3.jpg',
     'PUBLISHED', CURRENT_DATE - INTERVAL '40 days', CURRENT_TIMESTAMP, 1),

-- Video DRAFT trong tháng này (không nên hiển thị)
    ('Unfinished Project', 'Tech', 'Review', 'Advanced', 'Prototype demo',
     'audio4.mp3', 'video4.mp4', 0, 0, 0, 100, 2.0, 'thumb4.jpg',
     'DRAFT', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP, 2);
