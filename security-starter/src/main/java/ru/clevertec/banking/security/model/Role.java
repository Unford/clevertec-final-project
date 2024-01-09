package ru.clevertec.banking.security.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER,
    ADMIN,
    SUPER_USER;

    public String toUpperStringRole() {
        return "ROLE_" + this.name();
    }

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(toUpperStringRole());
    }
}
