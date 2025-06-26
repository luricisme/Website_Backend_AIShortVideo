package com.cabybara.aishortvideo.utils;

public enum UserRole {
    USER(1),
    ADMIN(2),
    UNDEFINED(0);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole fromValue(int value) {
        for (UserRole role : UserRole.values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole value: " + value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}