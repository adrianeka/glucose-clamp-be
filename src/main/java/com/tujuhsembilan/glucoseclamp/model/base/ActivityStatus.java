package com.tujuhsembilan.glucoseclamp.model.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityStatus {
    INQUEUE,
    IN_PROGRESS,
    COMPLETED;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static ActivityStatus fromValue(String value) {
        if (value == null) {
            return null;
        }

        return ActivityStatus.valueOf(value.trim().toUpperCase());
    }
}