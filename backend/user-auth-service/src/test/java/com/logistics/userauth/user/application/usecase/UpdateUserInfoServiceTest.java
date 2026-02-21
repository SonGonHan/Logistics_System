package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.command.UpdateUserInfoCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserInfoService - обновление данных профиля пользователя")
class UpdateUserInfoServiceTest {

    @Mock
    private UserRepository userRepository;

    private UpdateUserInfoService service;

    @BeforeEach
    void setUp() {
        service = new UpdateUserInfoService(userRepository);
    }

    @Test
    @DisplayName("Должен успешно обновить все поля профиля")
    void shouldUpdateAllFieldsSuccessfully() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("old@example.com")
                .phone("89991234567")
                .firstName("Старое")
                .lastName("Имя")
                .middleName("Отчество")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("new@example.com")
                .firstName("Новое")
                .lastName("Фамилия")
                .middleName("Новое отчество")
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result.email()).isEqualTo("new@example.com");
        assertThat(result.firstName()).isEqualTo("Новое");
        assertThat(result.lastName()).isEqualTo("Фамилия");
        assertThat(result.middleName()).isEqualTo("Новое отчество");
        assertThat(result.phone()).isEqualTo("89991234567"); // телефон не трогается

        verify(userRepository).save(argThat(u ->
                "new@example.com".equals(u.getEmail()) &&
                "Новое".equals(u.getFirstName()) &&
                "Фамилия".equals(u.getLastName()) &&
                "Новое отчество".equals(u.getMiddleName())
        ));
    }

    @Test
    @DisplayName("Должен выбросить 404, если пользователь не найден")
    void shouldThrow404WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        var command = UpdateUserInfoCommand.builder()
                .userId(999L)
                .email("any@example.com")
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getMessage()).contains("User not found");
                });

        verify(userRepository).findById(999L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Должен записать null-значения из команды в поля пользователя")
    void shouldOverwriteFieldsWithNullWhenCommandHasNulls() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("old@example.com")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email(null)
                .firstName(null)
                .lastName(null)
                .middleName(null)
                .build();

        // When
        var result = service.update(command);

        // Then — сервис передаёт null как есть, маппер отдаёт то, что сохранилось
        verify(userRepository).save(argThat(u ->
                u.getEmail() == null &&
                u.getFirstName() == null &&
                u.getLastName() == null &&
                u.getMiddleName() == null
        ));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Должен вернуть результат из репозитория после сохранения")
    void shouldReturnSavedUserMappedToResponse() {
        // Given
        var user = User.builder().id(1L).phone("89991234567").build();
        var saved = User.builder()
                .id(1L)
                .email("saved@example.com")
                .phone("89991234567")
                .firstName("Сохранённое")
                .lastName("Имя")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var command = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("saved@example.com")
                .firstName("Сохранённое")
                .lastName("Имя")
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result.email()).isEqualTo("saved@example.com");
        assertThat(result.firstName()).isEqualTo("Сохранённое");
        verify(userRepository, times(1)).save(any(User.class));
    }
}