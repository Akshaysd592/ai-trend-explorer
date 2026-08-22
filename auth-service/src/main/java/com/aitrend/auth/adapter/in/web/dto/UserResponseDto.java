package com.aitrend.auth.adapter.in.web.dto;

import com.aitrend.auth.domain.model.Role;
import java.time.LocalDateTime;
import java.util.Set;

public record UserResponseDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    Set<Role> roles,
    boolean enabled,
    LocalDateTime createdAt
) {}
