package com.cabybara.aishortvideo.utils;

import java.util.Map;

public class UtilMethods {
    public static String toFormData(Map<String, String> data) {
        return data.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + encode(entry.getValue()))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    public static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
