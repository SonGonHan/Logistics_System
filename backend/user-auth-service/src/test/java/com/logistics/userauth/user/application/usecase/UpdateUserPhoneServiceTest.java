package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPhoneCommand;
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
@DisplayName("UpdateUserPhoneService - Тесты обновления телефона пользователя")
class UpdateUserPhoneServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsRepository smsRepository;

    private UpdateUserPhoneService service;

    @BeforeEach
    void setUp() {
        service = new UpdateUserPhoneService(userRepository, smsRepository);
    }

    @Test
    @DisplayName("Должен успешно обновить телефон, когда номер подтвержден через SMS")
    void shouldUpdatePhoneWhenVerified() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .build();

        String newPhone = "89990000000";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(smsRepository.isVerified(newPhone)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPhoneCommand.builder()
                .userId(1L)
                .phone(newPhone)
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.phone()).isEqualTo(newPhone);

        verify(userRepository).findById(1L);
        verify(smsRepository).isVerified(newPhone);
        verify(smsRepository).deleteVerificationStatus(newPhone);
        verify(userRepository).save(argThat(u ->
                newPhone.equals(u.getPhone())
        ));
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда номер не подтвержден")
    void shouldThrowWhenPhoneNotVerified() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .build();

        String newPhone = "89990000000";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(smsRepository.isVerified(newPhone)).thenReturn(false);

        var command = UpdateUserPhoneCommand.builder()
                .userId(1L)
                .phone(newPhone)
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(PhoneNotVerifiedException.class)
                .hasMessageContaining("Phone is not verified");

        verify(smsRepository).isVerified(newPhone);
        verify(smsRepository, never()).deleteVerificationStatus(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен вернуть существующий профиль, если номер не изменился")
    void shouldReturnExistingProfileWhenPhoneNotChanged() {
        // Given
        String existingPhone = "89991234567";
        var user = User.builder()
                .id(1L)
                .phone(existingPhone)
                .firstName("Иван")
                .lastName("Иванов")
                .email("ivan@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPhoneCommand.builder()
                .userId(1L)
                .phone(existingPhone)
                .build();

        // When
        var result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.phone()).isEqualTo(existingPhone);

        verify(userRepository).findById(1L);
        verifyNoInteractions(smsRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Должен выбросить 404, когда пользователь не найден")
    void shouldThrow404WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        var command = UpdateUserPhoneCommand.builder()
                .userId(999L)
                .phone("89990000000")
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND);
                });

        verify(userRepository).findById(999L);
        verifyNoInteractions(smsRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Должен выбросить 400, когда номер телефона null")
    void shouldThrow400WhenPhoneIsNull() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPhoneCommand.builder()
                .userId(1L)
                .phone(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getMessage()).contains("Phone is required");
                });

        verifyNoInteractions(smsRepository);
    }

    @Test
    @DisplayName("Должен выбросить 400, когда номер телефона пустой")
    void shouldThrow400WhenPhoneIsBlank() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPhoneCommand.builder()
                .userId(1L)
                .phone("   ")
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    @DisplayName("Должен нормализовать телефонный номер перед проверкой")
    void shouldNormalizePhoneBeforeVerification() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .build();

        String inputPhone = "+7 999 000 00 00";
        String normalizedPhone = "89990000000";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(smsRepository.isVerified(normalizedPhone)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPhoneCommand.builder()
                .userId(1L)
                .phone(inputPhone)
                .build();

        // When
        service.update(command);

        // Then
        verify(smsRepository).isVerified(normalizedPhone);
        verify(smsRepository).deleteVerificationStatus(normalizedPhone);
    }
}
