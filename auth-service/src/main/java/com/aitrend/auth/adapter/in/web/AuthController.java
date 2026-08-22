package com.aitrend.auth.adapter.in.web;

import com.aitrend.auth.adapter.in.web.dto.*;
import com.aitrend.auth.application.port.in.*;
import com.aitrend.auth.application.port.out.UserRepositoryPort;
import com.aitrend.auth.domain.exception.InvalidCredentialsException;
import com.aitrend.auth.domain.model.AuthToken;
import com.aitrend.auth.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration, JWT credential authentication, token renewal, and profile operations.")
public class AuthController {

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

    @PostMapping("/register")
    @Operation(summary = "Register a new user account", description = "Creates a new user profile with ROLE_USER and BCrypt password hash.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid payload"),
            @ApiResponse(responseCode = "409", description = "Account already exists for given email")
    })
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.email(), request.password(), request.firstName(), request.lastName()
        );
        User registeredUser = registerUserUseCase.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponseDto(registeredUser));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate credentials and generate JWT tokens", description = "Validates user password and returns HMAC-SHA256 signed access & refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        AuthToken token = loginUseCase.login(command);
        User user = userRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        AuthResponseDto response = new AuthResponseDto(
                token.accessToken(),
                token.refreshToken(),
                token.tokenType(),
                token.expiresInSeconds(),
                toUserResponseDto(user)
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renew JWT access token", description = "Generates a new access token using a valid refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renewed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthToken token = refreshTokenUseCase.refreshToken(request.refreshToken());
        return ResponseEntity.ok(new AuthResponseDto(
                token.accessToken(),
                token.refreshToken(),
                token.tokenType(),
                token.expiresInSeconds(),
                null
        ));
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve authenticated user profile", description = "Extracts user context from validated JWT Bearer header.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile retrieved"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token")
    })
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
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
