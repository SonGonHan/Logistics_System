package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticateUserService: юнит-тесты")
public class AuthenticateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @Mock
    private CreateAuditLogUseCase createAuditLogUseCase;

    @Mock
    private InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @InjectMocks
    private AuthenticateUserService service;

    private User buildUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .phone("79991234567")
                .passwordHash("HASH")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .lastAccessedTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Должен аутентифицировать пользователя и вернуть JWT")
    void shouldAuthenticateUserAndReturnJwt() {
        // given
        var command = AuthenticateUserCommand.builder()
                .phone("79991234567")
                .password("Password123!")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla")
                .build();

        var user = buildUser();

        when(userRepository.findByPhone("79991234567")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "HASH")).thenReturn(true);
        when(tokenGenerator.generateAccessToken(any(User.class))).thenReturn("jwt-token");
        when(createRefreshTokenUseCase.create(any())).thenReturn("refresh-token");
        // when
        JwtAuthenticationResponse response = service.authenticate(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");

        verify(createRefreshTokenUseCase).create(any());
    }

    @Test
    @DisplayName("Должен бросить BadCredentialsException при неверном пароле")
    void shouldThrowWhenPasswordInvalid() {
        // given
        var command = AuthenticateUserCommand.builder()
                .phone("79991234567")
                .password("wrong")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla")
                .build();

        var user = buildUser();

        when(userRepository.findByPhone("79991234567")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        // then
        assertThatThrownBy(() -> service.authenticate(command))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Должен бросить BadCredentialsException, если пользователь не найден")
    void shouldThrowWhenUserNotFound() {
        // given
        var command = AuthenticateUserCommand.builder()
                .phone("79990000000")
                .password("Password123!")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla")
                .build();

        when(userRepository.findByPhone("79990000000")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.authenticate(command))
                .isInstanceOf(BadCredentialsException.class);
    }
}
