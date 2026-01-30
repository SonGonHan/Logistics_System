package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.port.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.domain.User;
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

    @Mock
    private CreateAuditLogUseCase createAuditLogUseCase;

    @InjectMocks
    private RevokeRefreshTokenService service;

    @Test
    @DisplayName("Должен отозвать валидный refresh токен")
    void shouldRevokeValidRefreshToken() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .build();

        var session = UserSession.builder()
                .id(1L)
                .user(user)
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
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Invalid refresh token");
    }
}
