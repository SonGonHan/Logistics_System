package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.command.GetUserInfoCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserInfoService - тестирование получения информации о пользователе")
class GetUserInfoServiceTest {

    @Mock
    private UserRepository repository;

    private GetUserInfoService service;

    @BeforeEach
    void setUp() {
        service = new GetUserInfoService(repository);
    }

    @Test
    @DisplayName("Возвращает UserResponse, если пользователь найден")
    void shouldReturnUserResponseWhenUserExists() {
        // Given
        Long userId = 1L;
        var user = User.builder()
                .id(userId)
                .email("john@example.com")
                .phone("79991234567")
                .firstName("John")
                .lastName("Doe")
                .middleName("M")
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // When
        var result = service.getUserInfo(new GetUserInfoCommand(userId));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.phone()).isEqualTo("79991234567");
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.middleName()).isEqualTo("M");

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Кидает 404, если пользователь не найден")
    void shouldThrow404WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.getUserInfo(new GetUserInfoCommand(userId)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(repository).findById(userId);
        verifyNoMoreInteractions(repository);
    }
}
