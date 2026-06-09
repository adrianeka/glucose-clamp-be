package com.tujuhsembilan.glucoseclamp.model.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserPositionName {
    SUPERADMIN ("SUPERADMIN"),
    ADMINISTRASI ("ADMINISTRASI"),
    DOKTER_PENELITI ("DOKTER PENELITI"),
    ANALIS ("ANALIS"),
    PERAWAT ("PERAWAT"),
    PERAWAT_DOKTER ("PERAWAT/DOKTER");

    private final String value;

    UserPositionName(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserPositionName fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replace('_', ' ').toUpperCase();
        for (UserPositionName position : values()) {
            if (position.value.equalsIgnoreCase(normalized) || position.name().equalsIgnoreCase(value.trim())) {
                return position;
            }
        }

        throw new IllegalArgumentException("Unknown user position: " + value);
    }
}