package com.aitrend.auth.adapter.out.jwt;

import com.aitrend.auth.application.port.out.TokenProviderPort;
import com.aitrend.auth.domain.model.AuthToken;
import com.aitrend.auth.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenAdapter implements TokenProviderPort {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenAdapter(
            @Value("${jwt.secret:aiTrendExplorerSuperSecretKeyThatIsAtLeast256BitsLongForHMACSHA256}") String secret,
            @Value("${jwt.expiration.access-token-ms:900000}") long accessTokenExpirationMs, // 15 mins
            @Value("${jwt.expiration.refresh-token-ms:604800000}") long refreshTokenExpirationMs // 7 days
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public AuthToken generateToken(User user) {
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + accessTokenExpirationMs);
        Date refreshExpiration = new Date(now.getTime() + refreshTokenExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());

        String accessToken = Jwts.builder()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(now)
                .expiration(accessExpiration)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(refreshExpiration)
                .signWith(key)
                .compact();

        return new AuthToken(accessToken, refreshToken, accessTokenExpirationMs / 1000);
    }

    @Override
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
