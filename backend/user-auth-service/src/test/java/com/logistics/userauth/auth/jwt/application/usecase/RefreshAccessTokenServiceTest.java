package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.RefreshAccessTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshAccessTokenService: юнит-тесты")
class RefreshAccessTokenServiceTest {

    @Mock
    private UserSessionRepository repository;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @Mock
    private InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @InjectMocks
    private RefreshAccessTokenService service;

    @Test
    @DisplayName("Должен обновить токены для валидного refresh токена")
    void shouldRefreshTokensForValidRefreshToken() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .role(UserRole.CLIENT)
                .build();

        var session = UserSession.builder()
                .id(1L)
                .user(user)
                .refreshToken("old-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();

        var command = RefreshAccessTokenCommand.builder()
                .refreshToken("old-refresh-token")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        when(repository.findByRefreshToken("old-refresh-token"))
                .thenReturn(Optional.of(session));
        when(tokenGenerator.generateAccessToken(user))
                .thenReturn("new-access-token");
        when(createRefreshTokenUseCase.create(any()))
                .thenReturn("new-refresh-token");

        // When
        var response = service.refresh(command);

        // Then
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");

        // Проверяем, что старая сессия отозвана
        verify(repository).save(argThat(s -> s.isRevoked()));

        // Проверяем, что создан новый refresh токен
        verify(createRefreshTokenUseCase).create(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение для невалидного refresh токена")
    void shouldThrowExceptionForInvalidRefreshToken() {
        // Given
        var command = RefreshAccessTokenCommand.builder()
                .refreshToken("invalid-token")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        when(repository.findByRefreshToken("invalid-token"))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> service.refresh(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Должен выбросить исключение для отозванного токена")
    void shouldThrowExceptionForRevokedToken() {
        // Given
        var session = UserSession.builder()
                .refreshToken("revoked-token")
                .revoked(true)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        var command = RefreshAccessTokenCommand.builder()
                .refreshToken("revoked-token")
                .build();

        when(repository.findByRefreshToken("revoked-token"))
                .thenReturn(Optional.of(session));

        // Then
        assertThatThrownBy(() -> service.refresh(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("revoked");
    }

    @Test
    @DisplayName("Должен выбросить исключение для истекшего токена")
    void shouldThrowExceptionForExpiredToken() {
        // Given
        var session = UserSession.builder()
                .refreshToken("expired-token")
                .revoked(false)
                .expiresAt(LocalDateTime.now().minusDays(1)) // Истёк вчера
                .build();

        var command = RefreshAccessTokenCommand.builder()
                .refreshToken("expired-token")
                .build();

        when(repository.findByRefreshToken("expired-token"))
                .thenReturn(Optional.of(session));

        // Then
        assertThatThrownBy(() -> service.refresh(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");
    }
}
