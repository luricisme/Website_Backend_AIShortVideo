package com.cabybara.aishortvideo.utils;

public enum UserStatus {
    ACTIVE(1),
    INACTIVE(0),
    DELETED(2);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown UserStatus value: " + value);
    }

    @Override
    public String toString() {
        return name();
    }
}
