package com.mach.bff.security.util;

import com.mach.bff.security.AuthenticationToken;
import com.mach.bff.security.model.UserPrincipal;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@NoArgsConstructor(access = PRIVATE)
public final class SecurityUtils {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String TOKEN_REQUEST_PREFIX = "Bearer ";

    public static UserPrincipal getUserPrincipal() {
        return (UserPrincipal) ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .orElse(null);
    }

    public static void setUserPrincipal(UserPrincipal userPrincipal) {
        setUserPrincipal(new AuthenticationToken(userPrincipal));
    }

    public static void setUserPrincipal(Authentication authenticationToken) {
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public static RequestMatcher antMatcher(HttpMethod method, String pattern) {
        return new AntPathRequestMatcher(pattern, method.name());
    }

    public static String extractTokenFromHeaders(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_REQUEST_PREFIX)) {
            return bearerToken.substring(TOKEN_REQUEST_PREFIX.length());
        }
        return EMPTY;
    }
}
