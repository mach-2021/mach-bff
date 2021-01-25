package com.mach.bff.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mach.bff.security.model.Authorities.CUSTOMER;
import static com.mach.bff.security.model.Authorities.GUEST;

@Data
@Builder
@ToString(doNotUseGetters = true)
public class UserPrincipal implements UserDetails {

    private String name;
    private String internalId;
    private String email;

    @JsonIgnore
    private List<GrantedAuthority> authorities;

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonProperty("roles")
    public List<String> getStringAuthorities() {
        return Optional.ofNullable(authorities)
                .orElse(Collections.emptyList())
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return name;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return null;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public UserPrincipal mergePrincipals(UserPrincipal updatedPrincipal) {
        cleanUserAuthority();
        authorities.addAll(updatedPrincipal.authorities);
        name = updatedPrincipal.name;
        email = updatedPrincipal.email;
        return this;
    }

    private void cleanUserAuthority() {
        authorities.remove(CUSTOMER.authority());
        authorities.remove(GUEST.authority());
    }

    public boolean hasAuthority(Authorities authority) {
        return this.authorities != null && this.authorities.contains(authority.authority());
    }

    public boolean hasAnyOfAuthorities(Authorities... authorities) {
        for (Authorities authority : authorities) {
            if (hasAuthority(authority)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyOfAuthorities(List<Authorities> authorities) {
        for (Authorities authority : authorities) {
            if (hasAuthority(authority)) {
                return true;
            }
        }
        return false;
    }
}
