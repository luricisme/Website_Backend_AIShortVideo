package com.cabybara.aishortvideo.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DataSource {
    Wikipedia("wikipedia"),
    Wikidata("wikidata"),
    AI("ai");

    private final String value;

    DataSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DataSource fromValue(String value) {
        for (DataSource source : DataSource.values()) {
            if (source.value.equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Invalid data source: " + value);
    }
}
