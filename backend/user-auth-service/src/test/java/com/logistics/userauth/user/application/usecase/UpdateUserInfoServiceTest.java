package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.sms.application.port.out.SmsRepository;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserInfoService")
class UpdateUserInfoServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsRepository smsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UpdateUserInfoService service;

    @BeforeEach
    void setUp() {
        service = new UpdateUserInfoService(userRepository, smsRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Кидает 404, если пользователь не найден")
    void shouldThrow404WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("a@b.com")
                .phone("79990000000")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .build();

        // When / Then
        assertThatThrownBy(() -> service.update(cmd))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(smsRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Обновляет основные поля, если пароль не меняем и телефон не меняется")
    void shouldUpdateProfileOnly() {
        // Given
        var user = User.builder()
                .id(1L)
                .email("old@mail.com")
                .phone("79991234567")
                .passwordHash("hash")
                .firstName("Old")
                .lastName("Old")
                .middleName("Old")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("new@mail.com")
                .phone("79991234567") // same phone
                .firstName("New")
                .lastName("Name")
                .middleName("Mid")
                .build();

        // When
        var res = service.update(cmd);

        // Then
        assertThat(res).isNotNull();
        assertThat(res.email()).isEqualTo("new@mail.com");
        assertThat(res.phone()).isEqualTo("79991234567");
        assertThat(res.firstName()).isEqualTo("New");
        assertThat(res.lastName()).isEqualTo("Name");
        assertThat(res.middleName()).isEqualTo("Mid");

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(smsRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Кидает 400, если newPassword указан, но oldPassword не передан")
    void shouldThrow400WhenOldPasswordMissing() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .passwordHash("oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("a@b.com")
                .phone("79991234567")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .oldPassword(null)
                .newPassword("NewPass123!")
                .build();

        // When / Then
        assertThatThrownBy(() -> service.update(cmd))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(smsRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Кидает 401, если oldPassword неверный")
    void shouldThrow401WhenOldPasswordInvalid() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .passwordHash("oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "oldHash")).thenReturn(false);

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("a@b.com")
                .phone("79991234567")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .oldPassword("wrong")
                .newPassword("NewPass123!")
                .build();

        // When / Then
        assertThatThrownBy(() -> service.update(cmd))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("wrong", "oldHash");
        verifyNoMoreInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(smsRepository);
    }

    @Test
    @DisplayName("Обновляет passwordHash, если oldPassword валидный")
    void shouldChangePasswordWhenOldPasswordValid() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .passwordHash("oldHash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass123!", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("newHash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("a@b.com")
                .phone("79991234567")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .oldPassword("OldPass123!")
                .newPassword("NewPass123!")
                .build();

        // When
        var res = service.update(cmd);

        // Then
        assertThat(res).isNotNull();
        verify(passwordEncoder).matches("OldPass123!", "oldHash");
        verify(passwordEncoder).encode("NewPass123!");
        verify(userRepository).save(argThat(u -> "newHash".equals(u.getPasswordHash())));
        verifyNoInteractions(smsRepository);
    }

    @Test
    @DisplayName("Обновляет телефон, если новый номер подтверждён по SMS")
    void shouldChangePhoneWhenVerified() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .passwordHash("hash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(smsRepository.isPhoneVerified("79990000000")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("a@b.com")
                .phone("79990000000")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .build();

        // When
        var res = service.update(cmd);

        // Then
        assertThat(res).isNotNull();
        assertThat(res.phone()).isEqualTo("79990000000");

        verify(smsRepository).isPhoneVerified("79990000000");
        verify(smsRepository).deleteVerificationStatus("79990000000");
        verify(userRepository).save(argThat(u -> "79990000000".equals(u.getPhone())));
    }

    @Test
    @DisplayName("Кидает ошибку, если новый номер не подтверждён по SMS")
    void shouldThrowWhenPhoneNotVerified() {
        // Given
        var user = User.builder()
                .id(1L)
                .phone("79991234567")
                .passwordHash("hash")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(smsRepository.isPhoneVerified("79990000000")).thenReturn(false);

        var cmd = UpdateUserInfoCommand.builder()
                .userId(1L)
                .email("a@b.com")
                .phone("79990000000")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .build();

        // When / Then
        assertThatThrownBy(() -> service.update(cmd))
                .isInstanceOf(PhoneNotVerifiedException.class);

        verify(smsRepository).isPhoneVerified("79990000000");
        verify(smsRepository, never()).deleteVerificationStatus(any());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}
