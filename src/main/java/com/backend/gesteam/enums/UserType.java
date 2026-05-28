package com.backend.gesteam.enums;

public enum UserType {
    CLUB("Club"),
    COACH("Entrenador"),
    PLAYER("Jugador"),
    USER("Usuario");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
