package com.tujuhsembilan.glucoseclamp.model.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionStatus {
    IN_QUEUE("IN QUEUE"),
    IN_PROGRESS("IN PROGRESS"),
    COMPLETED("COMPLETED");

    private final String value;

    SessionStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SessionStatus fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replace('_', ' ').toUpperCase();
        for (SessionStatus status : values()) {
            if (status.value.equalsIgnoreCase(normalized) || status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown session status: " + value);
    }
}