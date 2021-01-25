package com.mach.bff.security;

import com.mach.bff.security.model.Authorities;
import com.mach.bff.security.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class JwtTokenProvider {

    public static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";

    private static final String NAME = "name";
    private static final String INTERNAL_ID = "internalId";
    private static final String ROLES = "roles";
    private static final String EMAIL = "email";

    private final String jwtSecretKey;
    private final int jwtExpirationInMs;

    public JwtTokenProvider(
            @Value("${jwt.key}") String jwtSecretKey,
            @Value("${jwt.expiration-in-ms}") int jwtExpirationInMs) {
        this.jwtSecretKey = jwtSecretKey;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(final UserPrincipal userPrincipal) {
        log.debug("Start generating a token");
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim(NAME, userPrincipal.getName())
                .claim(INTERNAL_ID, userPrincipal.getInternalId())
                .claim(ROLES, userPrincipal.getStringAuthorities())
                .claim(EMAIL, userPrincipal.getEmail())
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public UserPrincipal getUserPrincipalFromJwt(final String token) {
        log.debug("Start get user principal from token");
        final Jws<Claims> claimsJws = parseJwt(token);
        final Claims claims = claimsJws.getBody();
        final List<GrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
        return UserPrincipal.builder()
                .name(claims.get(NAME, String.class))
                .internalId(claims.get(INTERNAL_ID, String.class))
                .email(claims.get(EMAIL, String.class))
                .authorities(authorities)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        List<String> stringAuthorities = ofNullable(claims.get(ROLES, List.class)).orElse(emptyList());
        return stringAuthorities.stream()
                .map(Authorities::valueOf)
                .map(Authorities::authority)
                .collect(Collectors.toList());
    }

    public boolean validateToken(final String token) {
        boolean isValidToken = false;
        log.debug("Start validate token");
        try {
            parseJwt(token);
            isValidToken = true;
        } catch (JwtException e) {
            log.error("Invalid or expired JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        return isValidToken;
    }

    private Jws<Claims> parseJwt(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token);
    }

    private <T> T getOrDefault(final T result, final T defaultResult) {
        return Objects.nonNull(result)
                ? result
                : defaultResult;
    }
}
