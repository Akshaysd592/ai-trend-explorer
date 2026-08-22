package com.aitrend.auth.application.port.in;

public record RegisterUserCommand(
    String email,
    String password,
    String firstName,
    String lastName
) {}
