package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeRefreshTokenService: юнит-тесты")
class RevokeRefreshTokenServiceTest {

    @Mock
    private UserSessionRepository repository;

    @InjectMocks
    private RevokeRefreshTokenService service;

    @Test
    @DisplayName("Должен отозвать валидный refresh токен")
    void shouldRevokeValidRefreshToken() {
        // Given
        var session = UserSession.builder()
                .id(1L)
                .refreshToken("valid-token")
                .revoked(false)
                .build();

        var command = RevokeRefreshTokenCommand.builder()
                .refreshToken("valid-token")
                .build();

        when(repository.findByRefreshToken("valid-token"))
                .thenReturn(Optional.of(session));

        // When
        service.revoke(command);

        // Then
        verify(repository).save(argThat(s -> s.isRevoked()));
    }

    @Test
    @DisplayName("Должен выбросить исключение для несуществующего токена")
    void shouldThrowExceptionForNonExistentToken() {
        // Given
        var command = RevokeRefreshTokenCommand.builder()
                .refreshToken("non-existent-token")
                .build();

        when(repository.findByRefreshToken("non-existent-token"))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> service.revoke(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid refresh token");
    }
}
