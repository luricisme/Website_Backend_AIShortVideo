package com.cabybara.aishortvideo.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    VIETNAMESE("vi"),
    ENGLISH("en");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Language fromValue(String value) {
        for (Language lang : Language.values()) {
            if (lang.value.equalsIgnoreCase(value)) {
                return lang;
            }
        }
//        throw new IllegalArgumentException("Invalid language: " + value);
        return null;
    }
}
