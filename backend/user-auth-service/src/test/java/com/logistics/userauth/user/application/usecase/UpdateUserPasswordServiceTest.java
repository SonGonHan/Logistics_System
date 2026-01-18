package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.command.UpdateUserPasswordCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserPasswordService - Тесты обновления пароля пользователя")
class UpdateUserPasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UpdateUserPasswordService service;

    @BeforeEach
    void setUp() {
        service = new UpdateUserPasswordService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Должен успешно изменить пароль, когда старый пароль верный")
    void shouldChangePasswordWhenOldPasswordValid() {
        // Given
        String oldPassword = "OldPass123!";
        String newPassword = "NewPass123!";
        String oldHash = "$2a$10$oldHash";
        String newHash = "$2a$10$newHash";

        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash(oldHash)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, oldHash)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newHash);

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();

        // When
        service.update(command);

        // Then
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(oldPassword, oldHash);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(argThat(u ->
                newHash.equals(u.getPasswordHash())
        ));
    }

    @Test
    @DisplayName("Должен выбросить BadCredentialsException, когда старый пароль неверный")
    void shouldThrowWhenOldPasswordInvalid() {
        // Given
        String oldPassword = "WrongPass123!";
        String newPassword = "NewPass123!";
        String oldHash = "$2a$10$oldHash";

        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash(oldHash)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, oldHash)).thenReturn(false);

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Неверный старый пароль");

        verify(passwordEncoder).matches(oldPassword, oldHash);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить 400, когда oldPassword отсутствует")
    void shouldThrow400WhenOldPasswordMissing() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash("$2a$10$oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword(null)
                .newPassword("NewPass123!")
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getMessage()).contains("Введите старый пароль");
                });

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить 400, когда oldPassword пустой")
    void shouldThrow400WhenOldPasswordBlank() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash("$2a$10$oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword("   ")
                .newPassword("NewPass123!")
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
    @DisplayName("Должен выбросить 400, когда newPassword отсутствует")
    void shouldThrow400WhenNewPasswordMissing() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash("$2a$10$oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword("OldPass123!")
                .newPassword(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getMessage()).contains("Введите новый пароль");
                });
    }

    @Test
    @DisplayName("Должен выбросить 400, когда newPassword пустой")
    void shouldThrow400WhenNewPasswordBlank() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash("$2a$10$oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword("OldPass123!")
                .newPassword("   ")
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
    @DisplayName("Должен выбросить 404, когда пользователь не найден")
    void shouldThrow404WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        var command = UpdateUserPasswordCommand.builder()
                .userId(999L)
                .oldPassword("OldPass123!")
                .newPassword("NewPass123!")
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
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Должен вызвать passwordEncoder.encode для нового пароля")
    void shouldEncodeNewPassword() {
        // Given
        String oldPassword = "OldPass123!";
        String newPassword = "NewPass123!";
        String oldHash = "$2a$10$oldHash";
        String newHash = "$2a$10$newHash";

        var user = User.builder()
                .id(1L)
                .phone("89991234567")
                .passwordHash(oldHash)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, oldHash)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newHash);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = UpdateUserPasswordCommand.builder()
                .userId(1L)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();

        // When
        service.update(command);

        // Then
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(argThat(u -> newHash.equals(u.getPasswordHash())));
    }
}
