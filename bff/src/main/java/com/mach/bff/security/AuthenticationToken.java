package com.mach.bff.security;

import com.mach.bff.security.model.UserPrincipal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AuthenticationToken extends AbstractAuthenticationToken {

    private UserPrincipal principal;

    public AuthenticationToken(final UserPrincipal principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
