package com.mach.bff.security.filter;

import com.mach.bff.security.AuthenticationToken;
import com.mach.bff.security.JwtTokenProvider;
import com.mach.bff.security.model.UserPrincipal;
import com.mach.bff.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.mach.bff.security.JwtTokenProvider.X_AUTH_TOKEN;
import static com.mach.bff.security.model.Authorities.GUEST;
import static com.mach.bff.security.util.SecurityUtils.PATH_MATCHER;
import static com.mach.bff.security.util.SecurityUtils.setUserPrincipal;
import static java.util.Objects.nonNull;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@Slf4j
public class JwtTokenFilter extends AbstractAuthenticationProcessingFilter {

    private static final String LOGOUT_URL_REGEX = "**/customers/logout";

    private JwtTokenProvider jwtTokenProvider;

    private JwtTokenFilter() {
        super("/**");
    }

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
//        String token = cookieService.extractTokenFromCookies(request);
        String token = SecurityUtils.extractTokenFromHeaders(request);
        return jwtTokenProvider.validateToken(token)
                ? createUserAuthenticationToken(token)
                : createGuestAuthenticationToken();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        log.debug("Authentication success. Updating SecurityContextHolder to contain authentication object");
        setUserPrincipal(authentication);
        if (nonNull(eventPublisher)) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, this.getClass()));
        }
        chain.doFilter(request, buildTokenHttpServletResponseWrapper(response));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.sendError(SC_UNAUTHORIZED, e.getMessage());
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("No additional operations required after properties set in {}. Skipping execution", getClass().getSimpleName());
    }

    private boolean isUrlMatch(HttpServletRequest request, String urlTemplate) {
        return PATH_MATCHER.match(urlTemplate, request.getRequestURL().toString());
    }

    private AuthenticationToken createUserAuthenticationToken(String token) {
        UserPrincipal userPrincipal = jwtTokenProvider.getUserPrincipalFromJwt(token);
        return new AuthenticationToken(userPrincipal);
    }

    private AnonymousAuthenticationToken createGuestAuthenticationToken() {
        UserPrincipal userPrincipal = buildGuestAuthenticationUserPrincipal();
        return new AnonymousAuthenticationToken(userPrincipal.getInternalId(), userPrincipal, userPrincipal.getAuthorities());
    }

    private UserPrincipal buildGuestAuthenticationUserPrincipal() {
        return UserPrincipal.builder()
                .authorities(List.of(GUEST.authority()))
                .internalId(UUID.randomUUID().toString())
                .build();
    }

    private HttpServletResponseWrapper buildTokenHttpServletResponseWrapper(HttpServletResponse response) {
        return new HttpServletResponseWrapper(response) {

            @Override
            public void setStatus(int value) {
                super.setStatus(value);
                populateToken();
            }

            @Override
            public void sendError(int value, String msg) throws IOException {
                super.sendError(value, msg);
                populateToken();
            }

            @Override
            public void sendError(int value) throws IOException {
                super.sendError(value);
                populateToken();
            }

            private void populateToken() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                    String token = jwtTokenProvider.generateToken(userPrincipal);
                    addHeader(X_AUTH_TOKEN, token);
                }
            }
        };
    }
}
