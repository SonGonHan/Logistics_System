package com.logistics.userauth.notification.email.application.usecase;

import com.logistics.userauth.notification.email.application.exception.EmailDeliveryException;
import com.logistics.userauth.notification.email.application.port.in.InternalEmailRateLimiterUseCase;
import com.logistics.userauth.notification.email.application.port.in.command.SendEmailVerificationCodeCommand;
import com.logistics.userauth.notification.email.application.port.out.EmailRepository;
import com.logistics.userauth.notification.email.application.port.out.SendEmailPort;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SendEmailVerificationCodeService - тестирование отправки Email кодов")
class SendEmailVerificationCodeServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private SendEmailPort sendEmailPort;

    @Mock
    private InternalEmailRateLimiterUseCase internalEmailRateLimiterUseCase;

    private SendEmailVerificationCodeService service;

    @BeforeEach
    void setUp() {
        service = new SendEmailVerificationCodeService(
                emailRepository,
                sendEmailPort,
                internalEmailRateLimiterUseCase
        );
        ReflectionTestUtils.setField(service, "codeLength", 6);
        ReflectionTestUtils.setField(service, "codeTtlMinutes", 5L);
    }

    @Test
    @DisplayName("Должен успешно отправить Email код")
    void shouldSuccessfullySendVerificationCode() {
        // Given
        String email = "user@example.com";
        var command = new SendEmailVerificationCodeCommand(email);

        when(sendEmailPort.sendVerificationCode(eq(email.toLowerCase()), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        ArgumentCaptor<EmailVerificationCode> codeCaptor = ArgumentCaptor.forClass(EmailVerificationCode.class);
        verify(emailRepository).save(codeCaptor.capture(), eq(5L));

        EmailVerificationCode savedCode = codeCaptor.getValue();
        assertThat(savedCode.getEmail()).isEqualTo(email.toLowerCase());
        assertThat(savedCode.getCode()).hasSize(6);
        assertThat(savedCode.getAttempts()).isZero();
        assertThat(savedCode.getExpiresAt()).isAfter(LocalDateTime.now());

        verify(sendEmailPort).sendVerificationCode(eq(email.toLowerCase()), anyString());
    }

    @Test
    @DisplayName("Должен проверить rate limit перед отправкой")
    void shouldCheckRateLimitBeforeSending() {
        // Given
        String email = "user@example.com";
        var command = new SendEmailVerificationCodeCommand(email);
        doNothing().when(internalEmailRateLimiterUseCase).checkRateLimiter(any());
        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        verify(internalEmailRateLimiterUseCase).checkRateLimiter(any());
    }

    @Test
    @DisplayName("Должен позволить отправку если старый код истек")
    void shouldAllowSendingIfOldCodeExpired() {
        // Given
        String email = "user@example.com";
        var command = new SendEmailVerificationCodeCommand(email);

        var expiredCode = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .attempts(0)
                .build();

        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        verify(sendEmailPort).sendVerificationCode(eq(email.toLowerCase()), anyString());
    }

    @Test
    @DisplayName("Должен бросить исключение если Email не удалось отправить")
    void shouldThrowExceptionIfEmailDeliveryFails() {
        // Given
        String email = "user@example.com";
        var command = new SendEmailVerificationCodeCommand(email);

        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.sendCode(command))
                .isInstanceOf(EmailDeliveryException.class)
                .hasMessageContaining("Не удалось отправить код");
    }

    @Test
    @DisplayName("Должен генерировать код заданной длины")
    void shouldGenerateCodeOfSpecifiedLength() {
        // Given
        String email = "user@example.com";
        var command = new SendEmailVerificationCodeCommand(email);

        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        var codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(sendEmailPort).sendVerificationCode(eq(email.toLowerCase()), codeCaptor.capture());

        String generatedCode = codeCaptor.getValue();
        assertThat(generatedCode).hasSize(6);
        assertThat(generatedCode).matches("\\d{6}");
    }

    @Test
    @DisplayName("Должен нормализовать email перед отправкой")
    void shouldNormalizeEmailBeforeSending() {
        // Given
        String email = "User@Example.COM";
        var command = new SendEmailVerificationCodeCommand(email);

        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        verify(sendEmailPort).sendVerificationCode(eq("user@example.com"), anyString());

        ArgumentCaptor<EmailVerificationCode> codeCaptor = ArgumentCaptor.forClass(EmailVerificationCode.class);
        verify(emailRepository).save(codeCaptor.capture(), eq(5L));
        assertThat(codeCaptor.getValue().getEmail()).isEqualTo("user@example.com");
    }
}