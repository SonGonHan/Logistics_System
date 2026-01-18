package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.command.UpdateUserPersonalInfoCommand;
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
@DisplayName("UpdateUserPersonalInfoService - Тесты обновления персональных данных")
class UpdateUserPersonalInfoServiceTest {

    @Mock
    private UserRepository userRepository;

    private UpdateUserPersonalInfoService service;

    @BeforeEach
    void setUp() {
        service = new UpdateUserPersonalInfoService(userRepository);
    }

    @Test
    @DisplayName("Должен успешно обновить все персональные данные пользователя")
    void shouldUpdateAllPersonalData() {
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

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email("new@example.com")
                .firstName("Новое")
                .lastName("Фамилия")
                .middleName("Новое отчество")
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("new@example.com");
        assertThat(result.firstName()).isEqualTo("Новое");
        assertThat(result.lastName()).isEqualTo("Фамилия");
        assertThat(result.middleName()).isEqualTo("Новое отчество");
        assertThat(result.phone()).isEqualTo("89991234567"); // Телефон не изменяется

        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(u ->
                "new@example.com".equals(u.getEmail()) &&
                        "Новое".equals(u.getFirstName()) &&
                        "Фамилия".equals(u.getLastName()) &&
                        "Новое отчество".equals(u.getMiddleName())
        ));
    }

    @Test
    @DisplayName("Должен обновить только email, если остальные поля null")
    void shouldUpdateOnlyEmail() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("old@example.com")
                .phone("89991234567")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email("new@example.com")
                .firstName(null)
                .lastName(null)
                .middleName(null)
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("new@example.com");

        verify(userRepository).save(argThat(u ->
                "new@example.com".equals(u.getEmail())
        ));
    }

    @Test
    @DisplayName("Должен обновить только имя и фамилию")
    void shouldUpdateOnlyNameFields() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("user@example.com")
                .phone("89991234567")
                .firstName("Старое")
                .lastName("Имя")
                .middleName("Отчество")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email(null)
                .firstName("Новое")
                .lastName("Фамилия")
                .middleName(null)
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Новое");
        assertThat(result.lastName()).isEqualTo("Фамилия");

        verify(userRepository).save(argThat(u ->
                "Новое".equals(u.getFirstName()) &&
                        "Фамилия".equals(u.getLastName())
        ));
    }

    @Test
    @DisplayName("Должен выбросить 404, когда пользователь не найден")
    void shouldThrow404WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(999L)
                .email("new@example.com")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
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
    @DisplayName("Должен сохранить пользователя через репозиторий")
    void shouldSaveUserThroughRepository() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("old@example.com")
                .phone("89991234567")
                .firstName("Старое")
                .lastName("Имя")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email("new@example.com")
                .firstName("Новое")
                .lastName("Фамилия")
                .middleName("Отчество")
                .build();

        // When
        service.update(command);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Должен вернуть UserInfoResponse с обновленными данными")
    void shouldReturnUserInfoResponseWithUpdatedData() {
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

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email("new@example.com")
                .firstName("Иван")
                .lastName("Петров")
                .middleName("Сергеевич")
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("new@example.com");
        assertThat(result.phone()).isEqualTo("89991234567");
        assertThat(result.firstName()).isEqualTo("Иван");
        assertThat(result.lastName()).isEqualTo("Петров");
        assertThat(result.middleName()).isEqualTo("Сергеевич");
    }

    @Test
    @DisplayName("Должен корректно обработать null значения в полях")
    void shouldHandleNullValuesCorrectly() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("user@example.com")
                .phone("89991234567")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email(null)
                .firstName(null)
                .lastName(null)
                .middleName(null)
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Должен сохранить пользователя и вернуть результат из репозитория")
    void shouldSaveAndReturnResultFromRepository() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("old@example.com")
                .phone("89991234567")
                .firstName("Старое")
                .lastName("Имя")
                .build();

        var savedUser = User.builder()
                .id(1L)
                .email("new@example.com")
                .phone("89991234567")
                .firstName("Новое")
                .lastName("Фамилия")
                .middleName("Отчество")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(1L)
                .email("new@example.com")
                .firstName("Новое")
                .lastName("Фамилия")
                .middleName("Отчество")
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("new@example.com");
        assertThat(result.firstName()).isEqualTo("Новое");

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
}
