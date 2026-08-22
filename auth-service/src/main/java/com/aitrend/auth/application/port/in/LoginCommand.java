package com.aitrend.auth.application.port.in;

public record LoginCommand(
    String email,
    String password
) {}
