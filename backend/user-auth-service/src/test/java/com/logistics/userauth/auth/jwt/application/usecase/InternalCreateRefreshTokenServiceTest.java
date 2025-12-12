// backend/user-auth-service/src/test/java/com/logistics/userauth/auth/jwt/application/usecase/InternalCreateRefreshTokenServiceTest.java
package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InternalCreateRefreshTokenService: юнит-тесты")
class InternalCreateRefreshTokenServiceTest {

    @Mock
    private UserSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InternalCreateRefreshTokenService service;

    @Test
    @DisplayName("Должен создать refresh токен для валидного пользователя")
    void shouldCreateRefreshTokenForValidUser() {
        // Given
        ReflectionTestUtils.setField(service, "refreshTokenTtlSeconds", 604800L);

        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .role(UserRole.CLIENT)
                .build();

        var command = CreateRefreshTokenCommand.builder()
                .userId(1L)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        String refreshToken = service.create(command);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();

        // Проверяем, что сессия сохранена
        ArgumentCaptor<UserSession> sessionCaptor = ArgumentCaptor.forClass(UserSession.class);
        verify(sessionRepository).save(sessionCaptor.capture());

        UserSession savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getUser()).isEqualTo(user);
        assertThat(savedSession.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(savedSession.isRevoked()).isFalse();
        assertThat(savedSession.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если пользователь не найден")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        var command = CreateRefreshTokenCommand.builder()
                .userId(999L)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Должен создать токен с IP и User-Agent")
    void shouldCreateTokenWithIpAndUserAgent() {
        // Given
        ReflectionTestUtils.setField(service, "refreshTokenTtlSeconds", 604800L);

        var user = User.builder().id(1L).build();
        var command = CreateRefreshTokenCommand.builder()
                .userId(1L)
                .ipAddress("10.0.0.1")
                .userAgent("Chrome/120")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        service.create(command);

        // Then
        ArgumentCaptor<UserSession> captor = ArgumentCaptor.forClass(UserSession.class);
        verify(sessionRepository).save(captor.capture());

        UserSession session = captor.getValue();
        assertThat(session.getIpAddress()).isNotNull();
        assertThat(session.getUserAgent()).isEqualTo("Chrome/120");
    }
}
