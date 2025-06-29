package com.cabybara.aishortvideo.utils;

// CREATE TYPE video_status AS ENUM ('DRAFT', 'PUBLISHED', 'DELETED', 'BLOCKED');
public enum VideoStatus {
    DRAFT("draft"),
    PUBLISHED("published"),
    DELETED("deleted"),
    BLOCKED("blocked");

    private final String value;

    VideoStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
