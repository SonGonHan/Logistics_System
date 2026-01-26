package com.logistics.userauth.notification.email.application.usecase;

import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.email.application.port.in.command.VerifyEmailCommand;
import com.logistics.userauth.notification.email.application.port.out.EmailRepository;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifyEmailService - тестирование проверки Email кодов")
class VerifyEmailServiceTest {

    @Mock
    private EmailRepository repository;

    private VerifyEmailService service;

    @BeforeEach
    void setUp() {
        service = new VerifyEmailService(repository);
        ReflectionTestUtils.setField(service, "maxAttempts", 3);
        ReflectionTestUtils.setField(service, "verifiedStatusTtlMinutes", 10L);
    }

    @Test
    @DisplayName("Должен успешно верифицировать корректный код")
    void shouldSuccessfullyVerifyValidCode() throws Throwable {
        // Given
        String email = "user@example.com";
        String code = "123456";
        var command = VerifyEmailCommand.builder()
                .email(email)
                .code(code)
                .build();

        var storedCode = EmailVerificationCode.builder()
                .id(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(repository.findById(email.toLowerCase())).thenReturn(Optional.of(storedCode));

        // When
        service.verify(command);

        // Then
        verify(repository).deleteVerificationCode(email.toLowerCase());
        verify(repository).markAsVerified(email.toLowerCase(), 10L);
    }

    @Test
    @DisplayName("Должен бросить исключение если код не найден")
    void shouldThrowExceptionIfCodeNotFound() {
        // Given
        String email = "user@example.com";
        var command = VerifyEmailCommand.builder()
                .email(email)
                .code("123456")
                .build();

        when(repository.findById(email.toLowerCase())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.verify(command))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }

    @Test
    @DisplayName("Должен бросить исключение если код истек")
    void shouldThrowExceptionIfCodeExpired() {
        // Given
        String email = "user@example.com";
        var command = VerifyEmailCommand.builder()
                .email(email)
                .code("123456")
                .build();

        var expiredCode = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .attempts(0)
                .build();

        when(repository.findById(email.toLowerCase())).thenReturn(Optional.of(expiredCode));

        // When & Then
        assertThatThrownBy(() -> service.verify(command))
                .isInstanceOf(InvalidVerificationCodeException.class);

        verify(repository).deleteVerificationCode(email.toLowerCase());
    }

    @Test
    @DisplayName("Должен удалить код при превышении максимального числа попыток")
    void shouldDeleteCodeWhenMaxAttemptsExceeded() {
        // Given
        String email = "user@example.com";
        var command = VerifyEmailCommand.builder()
                .email(email)
                .code("654321")
                .build();

        var storedCode = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(3)
                .build();

        when(repository.findById(email.toLowerCase())).thenReturn(Optional.of(storedCode));

        // When & Then
        assertThatThrownBy(() -> service.verify(command))
                .isInstanceOf(InvalidVerificationCodeException.class);

        verify(repository).incrementAttempts(email.toLowerCase());
        verify(repository).deleteVerificationCode(email.toLowerCase());
    }

    @Test
    @DisplayName("Должен нормализовать email перед проверкой")
    void shouldNormalizeEmailBeforeVerification() throws Throwable {
        // Given
        String email = "User@Example.COM";
        String code = "123456";
        var command = VerifyEmailCommand.builder()
                .email(email)
                .code(code)
                .build();

        var storedCode = EmailVerificationCode.builder()
                .id(email.toLowerCase())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(repository.findById("user@example.com")).thenReturn(Optional.of(storedCode));

        // When
        service.verify(command);

        // Then
        verify(repository).findById("user@example.com");
        verify(repository).deleteVerificationCode("user@example.com");
        verify(repository).markAsVerified("user@example.com", 10L);
    }
}