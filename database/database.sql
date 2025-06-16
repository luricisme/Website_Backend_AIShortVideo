-- 01. Tạo bảng users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR,
    last_name VARCHAR,
    email VARCHAR UNIQUE,
    role INTEGER,
    username VARCHAR UNIQUE,
    password VARCHAR,
    bio TEXT,
    avatar VARCHAR,
    facebook VARCHAR,
    instagram VARCHAR,
    tiktok VARCHAR,
    youtube VARCHAR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 02. Tạo bảng user_followers (mối quan hệ nhiều-nhiều giữa người dùng)
CREATE TABLE user_followers (
    id_user INTEGER,
    id_follower INTEGER,
    CHECK (id_user != id_follower),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user, id_follower),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_follower) REFERENCES users(id) ON DELETE CASCADE,
);

-- ENUM for Video's Status
CREATE TYPE video_status AS ENUM ('DRAFT', 'PUBLISHED', 'DELETED', 'BLOCKED');

-- Tạo bảng videos
CREATE TABLE videos (
    id SERIAL PRIMARY KEY,
    title VARCHAR,
    category VARCHAR,
    style VARCHAR,
    target VARCHAR,
    script TEXT,
    audio_url VARCHAR,
    video_url VARCHAR,
    like_cnt INTEGER DEFAULT 0,
    view_cnt INTEGER DEFAULT 0,
    length NUMERIC,
    thumbnail VARCHAR,
    status video_status DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_user INTEGER,
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE SET NULL
);

-- Tạo bảng liked_videos
CREATE TABLE liked_videos (
    id_user INTEGER,
    id_video INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user, id_video),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_video) REFERENCES videos(id) ON DELETE CASCADE
);

-- Tạo bảng video_images
CREATE TABLE video_images (
    id_video INTEGER NOT NULL,
    image_url VARCHAR NOT NULL,
    PRIMARY KEY (id_video, image_url),
    FOREIGN KEY (id_video) REFERENCES videos(id) ON DELETE CASCADE
);

-- Tạo bảng video_tags
CREATE TABLE video_tags (
    id_video INTEGER NOT NULL,
    tag_name VARCHAR NOT NULL,
    PRIMARY KEY (id_video, tag_name),
    FOREIGN KEY (id_video) REFERENCES videos(id) ON DELETE CASCADE
);

-- Tạo bảng commented_video
CREATE TABLE commented_video (
    id SERIAL PRIMARY KEY,
    id_video INTEGER,
    id_user INTEGER,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_video) REFERENCES videos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
);


