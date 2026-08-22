package com.aitrend.auth.domain.model;

import java.time.Instant;

public record AuthToken(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds,
    Instant expiresAt
) {
    public AuthToken(String accessToken, String refreshToken, long expiresInSeconds) {
        this(accessToken, refreshToken, "Bearer", expiresInSeconds, Instant.now().plusSeconds(expiresInSeconds));
    }
}
