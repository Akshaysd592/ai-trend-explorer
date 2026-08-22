package com.aitrend.auth.application.service;

import com.aitrend.auth.application.port.in.LoginCommand;
import com.aitrend.auth.application.port.in.RegisterUserCommand;
import com.aitrend.auth.application.port.out.PasswordEncoderPort;
import com.aitrend.auth.application.port.out.TokenProviderPort;
import com.aitrend.auth.application.port.out.UserRepositoryPort;
import com.aitrend.auth.domain.exception.InvalidCredentialsException;
import com.aitrend.auth.domain.exception.UserAlreadyExistsException;
import com.aitrend.auth.domain.model.AuthToken;
import com.aitrend.auth.domain.model.Role;
import com.aitrend.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    @Mock
    private TokenProviderPort tokenProviderPort;

    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        authApplicationService = new AuthApplicationService(userRepositoryPort, passwordEncoderPort, tokenProviderPort);
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand("test@aitrend.com", "securePassword123", "John", "Doe");

        when(userRepositoryPort.existsByEmail("test@aitrend.com")).thenReturn(false);
        when(passwordEncoderPort.encode("securePassword123")).thenReturn("encodedPasswordHash");
        
        User savedUser = new User(1L, "test@aitrend.com", "encodedPasswordHash", "John", "Doe",
                Set.of(Role.ROLE_USER), true, LocalDateTime.now(), LocalDateTime.now());
        
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = authApplicationService.registerUser(command);

        assertThat(result.getEmail()).isEqualTo("test@aitrend.com");
        assertThat(result.getRoles()).contains(Role.ROLE_USER);
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingEmail() {
        RegisterUserCommand command = new RegisterUserCommand("existing@aitrend.com", "password", "Jane", "Doe");
        when(userRepositoryPort.existsByEmail("existing@aitrend.com")).thenReturn(true);

        assertThatThrownBy(() -> authApplicationService.registerUser(command))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("existing@aitrend.com");
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginCommand command = new LoginCommand("test@aitrend.com", "correctPassword");
        User user = new User(1L, "test@aitrend.com", "encodedHash", "John", "Doe",
                Set.of(Role.ROLE_USER), true, LocalDateTime.now(), LocalDateTime.now());

        when(userRepositoryPort.findByEmail("test@aitrend.com")).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches("correctPassword", "encodedHash")).thenReturn(true);
        
        AuthToken expectedToken = new AuthToken("mockAccessToken", "mockRefreshToken", 900);
        when(tokenProviderPort.generateToken(user)).thenReturn(expectedToken);

        AuthToken result = authApplicationService.login(command);

        assertThat(result.accessToken()).isEqualTo("mockAccessToken");
    }

    @Test
    void shouldThrowExceptionOnInvalidPassword() {
        LoginCommand command = new LoginCommand("test@aitrend.com", "wrongPassword");
        User user = new User(1L, "test@aitrend.com", "encodedHash", "John", "Doe",
                Set.of(Role.ROLE_USER), true, LocalDateTime.now(), LocalDateTime.now());

        when(userRepositoryPort.findByEmail("test@aitrend.com")).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches("wrongPassword", "encodedHash")).thenReturn(false);

        assertThatThrownBy(() -> authApplicationService.login(command))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
