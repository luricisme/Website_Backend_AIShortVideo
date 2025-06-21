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

    public String getValue() {
        return value;
    }
}
