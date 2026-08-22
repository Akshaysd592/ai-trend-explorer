package com.aitrend.auth.adapter.in.web;

import com.aitrend.auth.application.port.in.*;
import com.aitrend.auth.application.port.out.UserRepositoryPort;
import com.aitrend.auth.domain.exception.InvalidCredentialsException;
import com.aitrend.auth.domain.model.AuthToken;
import com.aitrend.auth.domain.model.User;
import com.aitrend.auth.infrastructure.openapi.api.AuthenticationApi;
import com.aitrend.auth.infrastructure.openapi.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthenticationApi {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final UserRepositoryPort userRepositoryPort;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          UserRepositoryPort userRepositoryPort) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.getEmail(), request.getPassword(), request.getFirstName(), request.getLastName()
        );
        User registeredUser = registerUserUseCase.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponseDto(registeredUser));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto request) {
        LoginCommand command = new LoginCommand(request.getEmail(), request.getPassword());
        AuthToken token = loginUseCase.login(command);
        User user = userRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        AuthResponseDto response = new AuthResponseDto()
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .tokenType(token.tokenType())
                .expiresInSeconds((int) token.expiresInSeconds())
                .user(toUserResponseDto(user));

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthToken token = refreshTokenUseCase.refreshToken(request.getRefreshToken());
        AuthResponseDto response = new AuthResponseDto()
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .tokenType(token.tokenType())
                .expiresInSeconds((int) token.expiresInSeconds());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Authenticated user not found"));

        return ResponseEntity.ok(toUserResponseDto(user));
    }

    private UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        if (user.getCreatedAt() != null) {
            dto.setCreatedAt(user.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
        }
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream().map(r -> {
                try {
                    return Role.fromValue(r.name());
                } catch (Exception e) {
                    return Role.USER;
                }
            }).toList());
        }
        return dto;
    }
}
