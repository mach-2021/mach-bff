package com.mach.bff.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Authorities {

    CUSTOMER, GUEST;

    private final GrantedAuthority authority;

    Authorities() {
        authority = new SimpleGrantedAuthority(name());
    }

    public GrantedAuthority authority() {
        return authority;
    }
}
