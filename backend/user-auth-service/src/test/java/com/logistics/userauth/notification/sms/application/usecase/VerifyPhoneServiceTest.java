package com.logistics.userauth.notification.sms.application.usecase;

import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.sms.application.port.in.command.VerifyPhoneCommand;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
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
@DisplayName("VerifyPhoneService - тестирование проверки SMS кодов")
class VerifyPhoneServiceTest {

    @Mock
    private SmsRepository repository;

    private VerifyPhoneService service;

    @BeforeEach
    void setUp() {
        service = new VerifyPhoneService(repository);
        ReflectionTestUtils.setField(service, "maxAttempts", 3);
        ReflectionTestUtils.setField(service, "verifiedStatusTtlMinutes", 10L);
    }

    @Test
    @DisplayName("Должен успешно верифицировать корректный код")
    void shouldSuccessfullyVerifyValidCode() throws Throwable {
        // Given
        String phone = "89991234567";
        String code = "123456";
        var command = VerifyPhoneCommand.builder()
                .phone(phone)
                .code(code)
                .build();

        var storedCode = SmsVerificationCode.builder()
                .id(phone)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(repository.findById(phone)).thenReturn(Optional.of(storedCode));

        // When
        service.verify(command);

        // Then
        verify(repository).deleteVerificationCode(phone);
        verify(repository).markAsVerified(phone, 10L);
    }

    @Test
    @DisplayName("Должен бросить исключение если код не найден")
    void shouldThrowExceptionIfCodeNotFound() {
        // Given
        String phone = "89991234567";
        var command = VerifyPhoneCommand.builder()
                .phone(phone)
                .code("123456")
                .build();

        when(repository.findById(phone)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.verify(command))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }

    @Test
    @DisplayName("Должен бросить исключение если код истек")
    void shouldThrowExceptionIfCodeExpired() {
        // Given
        String phone = "89991234567";
        var command = VerifyPhoneCommand.builder()
                .phone(phone)
                .code("123456")
                .build();

        var expiredCode = SmsVerificationCode.builder()
                .id(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .attempts(0)
                .build();

        when(repository.findById(phone)).thenReturn(Optional.of(expiredCode));

        // When & Then
        assertThatThrownBy(() -> service.verify(command))
                .isInstanceOf(InvalidVerificationCodeException.class);

        verify(repository).deleteVerificationCode(phone);
    }

    @Test
    @DisplayName("Должен удалить код при превышении максимального числа попыток")
    void shouldDeleteCodeWhenMaxAttemptsExceeded() {
        // Given
        String phone = "89991234567";
        var command = VerifyPhoneCommand.builder()
                .phone(phone)
                .code("654321")
                .build();

        var storedCode = SmsVerificationCode.builder()
                .id(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(3)
                .build();

        when(repository.findById(phone)).thenReturn(Optional.of(storedCode));

        // When & Then
        assertThatThrownBy(() -> service.verify(command))
                .isInstanceOf(InvalidVerificationCodeException.class);

        verify(repository).incrementAttempts(phone);
        verify(repository).deleteVerificationCode(phone);
    }

}
