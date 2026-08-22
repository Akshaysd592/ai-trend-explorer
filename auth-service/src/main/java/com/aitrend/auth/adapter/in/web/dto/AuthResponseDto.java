package com.aitrend.auth.adapter.in.web.dto;

public record AuthResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInSeconds,
    UserResponseDto user
) {}
