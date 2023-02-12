package fr.openobservatory.backend.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountType {
    USER(true),
    ADMIN(false);

    private final boolean assignable;

    public boolean isAssignable() {
        return assignable;
    }
}
