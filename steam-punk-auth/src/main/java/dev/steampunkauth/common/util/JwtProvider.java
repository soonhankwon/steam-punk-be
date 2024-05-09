package dev.steampunkauth.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.issuer}")
    private String issuer;

    @Value("${spring.jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Value("${spring.jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public static final String JWT_PREFIX = "Bearer ";

    public String createJwt(Long userId, boolean isAccessToken) {
        Instant now = Instant.now();
        Map<String, Object> claims = createClaims(userId);
        int tokenExpirationMinutes = getTokenExpirationMinutes(isAccessToken);
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES)))
                .signWith(getSecretKey())
                .compact();
    }

    public String createDefaultToken(boolean isAccessToken) {
        Instant now = Instant.now();
        int tokenExpirationMinutes = getTokenExpirationMinutes(isAccessToken);
        return Jwts.builder()
                .issuer(issuer)
                .subject(issuer)
                .claims(Map.of())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES)))
                .signWith(getSecretKey())
                .compact();
    }

    private int getTokenExpirationMinutes(boolean isAccessToken) {
        return isAccessToken ? accessTokenExpirationMinutes : refreshTokenExpirationMinutes;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> createClaims(Long userId) {
        return Map.of(
                "id", userId
        );
    }
}
