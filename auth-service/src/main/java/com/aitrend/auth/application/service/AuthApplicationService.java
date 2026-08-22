package com.aitrend.auth.application.service;

import com.aitrend.auth.application.port.in.*;
import com.aitrend.auth.application.port.out.PasswordEncoderPort;
import com.aitrend.auth.application.port.out.TokenProviderPort;
import com.aitrend.auth.application.port.out.UserRepositoryPort;
import com.aitrend.auth.domain.exception.InvalidCredentialsException;
import com.aitrend.auth.domain.exception.UserAlreadyExistsException;
import com.aitrend.auth.domain.model.AuthToken;
import com.aitrend.auth.domain.model.Role;
import com.aitrend.auth.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Transactional
public class AuthApplicationService implements RegisterUserUseCase, LoginUseCase, RefreshTokenUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public AuthApplicationService(UserRepositoryPort userRepositoryPort,
                                  PasswordEncoderPort passwordEncoderPort,
                                  TokenProviderPort tokenProviderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public User registerUser(RegisterUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String encodedPassword = passwordEncoderPort.encode(command.password());

        User newUser = new User(
                null,
                command.email(),
                encodedPassword,
                command.firstName(),
                command.lastName(),
                Set.of(Role.ROLE_USER),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return userRepositoryPort.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthToken login(LoginCommand command) {
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoderPort.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("User account is disabled");
        }

        return tokenProviderPort.generateToken(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthToken refreshToken(String refreshToken) {
        if (!tokenProviderPort.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String email = tokenProviderPort.extractEmail(refreshToken);
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found for token"));

        return tokenProviderPort.generateToken(user);
    }
}
