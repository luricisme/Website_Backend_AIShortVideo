--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2 (Debian 17.2-1.pgdg120+1)
-- Dumped by pg_dump version 17.2

-- Started on 2025-07-09 21:34:48

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- TOC entry 867 (class 1247 OID 16550)
-- Name: video_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.video_status AS ENUM (
    'DRAFT',
    'PUBLISHED',
    'DELETED',
    'BLOCKED'
);


--
-- TOC entry 232 (class 1255 OID 16677)
-- Name: update_video_counters(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.update_video_counters() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


--
-- TOC entry 226 (class 1259 OID 16620)
-- Name: commented_video; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.commented_video (
    id integer NOT NULL,
    id_video integer,
    id_user integer,
    content text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 225 (class 1259 OID 16619)
-- Name: commented_video_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.commented_video_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3477 (class 0 OID 0)
-- Dependencies: 225
-- Name: commented_video_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.commented_video_id_seq OWNED BY public.commented_video.id;


--
-- TOC entry 229 (class 1259 OID 16659)
-- Name: disliked_videos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.disliked_videos (
    id_user integer NOT NULL,
    id_video integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 222 (class 1259 OID 16578)
-- Name: liked_videos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.liked_videos (
    id_user integer NOT NULL,
    id_video integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 231 (class 1259 OID 16682)
-- Name: published_video; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.published_video (
    id bigint NOT NULL,
    video_id character varying(255) NOT NULL,
    channel_id character varying(255) NOT NULL,
    channel_title character varying(255),
    title character varying(255) NOT NULL,
    description text,
    published_at timestamp with time zone NOT NULL,
    thumbnail_url character varying(255),
    view_count bigint DEFAULT 0,
    like_count bigint DEFAULT 0,
    dislike_count bigint DEFAULT 0,
    comment_count bigint DEFAULT 0,
    upload_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    last_updated timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    upload_by_id integer NOT NULL,
    platform character varying(255)
);


--
-- TOC entry 230 (class 1259 OID 16681)
-- Name: published_video_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.published_video_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3478 (class 0 OID 0)
-- Dependencies: 230
-- Name: published_video_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.published_video_id_seq OWNED BY public.published_video.id;


--
-- TOC entry 219 (class 1259 OID 16531)
-- Name: user_followers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_followers (
    id_user integer NOT NULL,
    id_follower integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_followers_check CHECK ((id_user <> id_follower))
);


--
-- TOC entry 228 (class 1259 OID 16641)
-- Name: user_social_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_social_accounts (
    id integer NOT NULL,
    user_id integer,
    platform character varying,
    platform_user_id character varying,
    access_token character varying,
    refresh_token character varying,
    expires_at timestamp without time zone,
    scope character varying(255),
    token_type character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- TOC entry 227 (class 1259 OID 16640)
-- Name: user_social_accounts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_social_accounts_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3479 (class 0 OID 0)
-- Dependencies: 227
-- Name: user_social_accounts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.user_social_accounts_id_seq OWNED BY public.user_social_accounts.id;


--
-- TOC entry 218 (class 1259 OID 16517)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id integer NOT NULL,
    first_name character varying,
    last_name character varying,
    email character varying,
    role integer,
    username character varying,
    password character varying,
    bio text,
    avatar character varying,
    facebook character varying,
    instagram character varying,
    tiktok character varying,
    youtube character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    status character varying(255) DEFAULT 'ACTIVE'::character varying
);


--
-- TOC entry 217 (class 1259 OID 16516)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3480 (class 0 OID 0)
-- Dependencies: 217
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 223 (class 1259 OID 16595)
-- Name: video_images; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.video_images (
    id_video integer NOT NULL,
    image_url character varying NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16607)
-- Name: video_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.video_tags (
    id_video integer NOT NULL,
    tag_name character varying NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 16560)
-- Name: videos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.videos (
    id integer NOT NULL,
    title character varying,
    category character varying,
    style character varying,
    target character varying,
    script text,
    audio_url character varying,
    video_url character varying,
    like_cnt integer DEFAULT 0,
    view_cnt integer DEFAULT 0,
    length numeric,
    thumbnail character varying,
    status public.video_status DEFAULT 'DRAFT'::public.video_status,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    id_user integer,
    dislike_cnt integer DEFAULT 0,
    comment_cnt integer DEFAULT 0
);


--
-- TOC entry 220 (class 1259 OID 16559)
-- Name: videos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.videos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3481 (class 0 OID 0)
-- Dependencies: 220
-- Name: videos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.videos_id_seq OWNED BY public.videos.id;


--
-- TOC entry 3270 (class 2604 OID 16623)
-- Name: commented_video id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commented_video ALTER COLUMN id SET DEFAULT nextval('public.commented_video_id_seq'::regclass);


--
-- TOC entry 3278 (class 2604 OID 16685)
-- Name: published_video id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.published_video ALTER COLUMN id SET DEFAULT nextval('public.published_video_id_seq'::regclass);


--
-- TOC entry 3273 (class 2604 OID 16644)
-- Name: user_social_accounts id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_social_accounts ALTER COLUMN id SET DEFAULT nextval('public.user_social_accounts_id_seq'::regclass);


--
-- TOC entry 3254 (class 2604 OID 16520)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3260 (class 2604 OID 16563)
-- Name: videos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.videos ALTER COLUMN id SET DEFAULT nextval('public.videos_id_seq'::regclass);


--
-- TOC entry 3303 (class 2606 OID 16629)
-- Name: commented_video commented_video_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commented_video
    ADD CONSTRAINT commented_video_pkey PRIMARY KEY (id);


--
-- TOC entry 3307 (class 2606 OID 16665)
-- Name: disliked_videos disliked_videos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disliked_videos
    ADD CONSTRAINT disliked_videos_pkey PRIMARY KEY (id_user, id_video);


--
-- TOC entry 3297 (class 2606 OID 16584)
-- Name: liked_videos liked_videos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.liked_videos
    ADD CONSTRAINT liked_videos_pkey PRIMARY KEY (id_user, id_video);


--
-- TOC entry 3309 (class 2606 OID 16695)
-- Name: published_video published_video_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.published_video
    ADD CONSTRAINT published_video_pkey PRIMARY KEY (id);


--
-- TOC entry 3311 (class 2606 OID 16697)
-- Name: published_video published_video_video_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.published_video
    ADD CONSTRAINT published_video_video_id_key UNIQUE (video_id);


--
-- TOC entry 3293 (class 2606 OID 16538)
-- Name: user_followers user_followers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_followers
    ADD CONSTRAINT user_followers_pkey PRIMARY KEY (id_user, id_follower);


--
-- TOC entry 3305 (class 2606 OID 16650)
-- Name: user_social_accounts user_social_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_social_accounts
    ADD CONSTRAINT user_social_accounts_pkey PRIMARY KEY (id);


--
-- TOC entry 3287 (class 2606 OID 16528)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 3289 (class 2606 OID 16526)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3291 (class 2606 OID 16530)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 3299 (class 2606 OID 16601)
-- Name: video_images video_images_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.video_images
    ADD CONSTRAINT video_images_pkey PRIMARY KEY (id_video, image_url);


--
-- TOC entry 3301 (class 2606 OID 16613)
-- Name: video_tags video_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.video_tags
    ADD CONSTRAINT video_tags_pkey PRIMARY KEY (id_video, tag_name);


--
-- TOC entry 3295 (class 2606 OID 16572)
-- Name: videos videos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT videos_pkey PRIMARY KEY (id);


--
-- TOC entry 3325 (class 2620 OID 16680)
-- Name: commented_video trg_commented_video_update; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_commented_video_update AFTER INSERT OR DELETE ON public.commented_video FOR EACH ROW EXECUTE FUNCTION public.update_video_counters();


--
-- TOC entry 3326 (class 2620 OID 16679)
-- Name: disliked_videos trg_disliked_videos_update; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_disliked_videos_update AFTER INSERT OR DELETE ON public.disliked_videos FOR EACH ROW EXECUTE FUNCTION public.update_video_counters();


--
-- TOC entry 3324 (class 2620 OID 16678)
-- Name: liked_videos trg_liked_videos_update; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_liked_videos_update AFTER INSERT OR DELETE ON public.liked_videos FOR EACH ROW EXECUTE FUNCTION public.update_video_counters();


--
-- TOC entry 3319 (class 2606 OID 16635)
-- Name: commented_video commented_video_id_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commented_video
    ADD CONSTRAINT commented_video_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3320 (class 2606 OID 16630)
-- Name: commented_video commented_video_id_video_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commented_video
    ADD CONSTRAINT commented_video_id_video_fkey FOREIGN KEY (id_video) REFERENCES public.videos(id) ON DELETE CASCADE;


--
-- TOC entry 3321 (class 2606 OID 16666)
-- Name: disliked_videos disliked_videos_id_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disliked_videos
    ADD CONSTRAINT disliked_videos_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3322 (class 2606 OID 16671)
-- Name: disliked_videos disliked_videos_id_video_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disliked_videos
    ADD CONSTRAINT disliked_videos_id_video_fkey FOREIGN KEY (id_video) REFERENCES public.videos(id) ON DELETE CASCADE;


--
-- TOC entry 3315 (class 2606 OID 16585)
-- Name: liked_videos liked_videos_id_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.liked_videos
    ADD CONSTRAINT liked_videos_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3316 (class 2606 OID 16590)
-- Name: liked_videos liked_videos_id_video_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.liked_videos
    ADD CONSTRAINT liked_videos_id_video_fkey FOREIGN KEY (id_video) REFERENCES public.videos(id) ON DELETE CASCADE;


--
-- TOC entry 3323 (class 2606 OID 16700)
-- Name: published_video published_video_upload_by_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.published_video
    ADD CONSTRAINT published_video_upload_by_id_fkey FOREIGN KEY (upload_by_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- TOC entry 3312 (class 2606 OID 16544)
-- Name: user_followers user_followers_id_follower_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_followers
    ADD CONSTRAINT user_followers_id_follower_fkey FOREIGN KEY (id_follower) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3313 (class 2606 OID 16539)
-- Name: user_followers user_followers_id_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_followers
    ADD CONSTRAINT user_followers_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3317 (class 2606 OID 16602)
-- Name: video_images video_images_id_video_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.video_images
    ADD CONSTRAINT video_images_id_video_fkey FOREIGN KEY (id_video) REFERENCES public.videos(id) ON DELETE CASCADE;


--
-- TOC entry 3318 (class 2606 OID 16614)
-- Name: video_tags video_tags_id_video_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.video_tags
    ADD CONSTRAINT video_tags_id_video_fkey FOREIGN KEY (id_video) REFERENCES public.videos(id) ON DELETE CASCADE;


--
-- TOC entry 3314 (class 2606 OID 16573)
-- Name: videos videos_id_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT videos_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE SET NULL;


-- Completed on 2025-07-09 21:35:05

--
-- PostgreSQL database dump complete
--

