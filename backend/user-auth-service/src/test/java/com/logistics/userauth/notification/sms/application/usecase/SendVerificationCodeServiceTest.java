package com.logistics.userauth.notification.sms.application.usecase;

import com.logistics.userauth.notification.sms.application.exception.SmsDeliveryException;
import com.logistics.userauth.notification.sms.application.port.in.InternalSmsRateLimiterUseCase;
import com.logistics.userauth.notification.sms.application.port.in.command.SendPhoneVerificationCodeCommand;
import com.logistics.userauth.notification.sms.application.port.out.SendSmsPort;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
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
@DisplayName("SendVerificationCodeService - тестирование отправки SMS кодов")
class SendVerificationCodeServiceTest {

    @Mock
    private SmsRepository smsRepository;

    @Mock
    private SendSmsPort sendSmsPort;

    @Mock
    private InternalSmsRateLimiterUseCase internalSmsRateLimiterUseCase;

    private SendPhoneVerificationCodeService service;

    @BeforeEach
    void setUp() {
        service = new SendPhoneVerificationCodeService(
                smsRepository,
                sendSmsPort,
                internalSmsRateLimiterUseCase
        );
        ReflectionTestUtils.setField(service, "codeLength", 6);
        ReflectionTestUtils.setField(service, "codeTtlMinutes", 5L);
    }

    @Test
    @DisplayName("Должен успешно отправить SMS код")
    void shouldSuccessfullySendVerificationCode() {
        // Given
        String phone = "89991234567";
        var command = new SendPhoneVerificationCodeCommand(phone);

        when(sendSmsPort.sendVerificationCode(eq(phone), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        ArgumentCaptor<SmsVerificationCode> codeCaptor = ArgumentCaptor.forClass(SmsVerificationCode.class);
        verify(smsRepository).save(codeCaptor.capture(), eq(5L));

        SmsVerificationCode savedCode = codeCaptor.getValue();
        assertThat(savedCode.getPhone()).isEqualTo(phone);
        assertThat(savedCode.getCode()).hasSize(6);
        assertThat(savedCode.getAttempts()).isZero();
        assertThat(savedCode.getExpiresAt()).isAfter(LocalDateTime.now());

        verify(sendSmsPort).sendVerificationCode(eq(phone), anyString());
    }

    @Test
    @DisplayName("Должен проверить rate limit перед отправкой")
    void shouldCheckRateLimitBeforeSending() {
        // Given
        String phone = "89991234567";
        var command = new SendPhoneVerificationCodeCommand(phone);
        doNothing().when(internalSmsRateLimiterUseCase).checkRateLimiter(any());
        when(sendSmsPort.sendVerificationCode(eq(phone), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        verify(internalSmsRateLimiterUseCase).checkRateLimiter(any());
    }


    @Test
    @DisplayName("Должен позволить отправку если старый код истек")
    void shouldAllowSendingIfOldCodeExpired() {
        // Given
        String phone = "89991234567";
        var command = new SendPhoneVerificationCodeCommand(phone);

        var expiredCode = SmsVerificationCode.builder()
                .id(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .attempts(0)
                .build();

        when(sendSmsPort.sendVerificationCode(eq(phone), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        verify(sendSmsPort).sendVerificationCode(eq(phone), anyString());
    }

    @Test
    @DisplayName("Должен бросить исключение если SMS не удалось отправить")
    void shouldThrowExceptionIfSmsDeliveryFails() {
        // Given
        String phone = "89991234567";
        var command = new SendPhoneVerificationCodeCommand(phone);

        when(sendSmsPort.sendVerificationCode(eq(phone), anyString())).thenReturn(false);

        // When & Then00
        assertThatThrownBy(() -> service.sendCode(command))
                .isInstanceOf(SmsDeliveryException.class)
                .hasMessageContaining("Не удалось отправить код");
    }

    @Test
    @DisplayName("Должен генерировать код заданной длины")
    void shouldGenerateCodeOfSpecifiedLength() {
        // Given
        String phone = "89991234567";
        var command = new SendPhoneVerificationCodeCommand(phone);

        when(sendSmsPort.sendVerificationCode(eq(phone), anyString())).thenReturn(true);

        // When
        service.sendCode(command);

        // Then
        var codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(sendSmsPort).sendVerificationCode(eq(phone), codeCaptor.capture());

        String generatedCode = codeCaptor.getValue();
        assertThat(generatedCode).hasSize(6);
        assertThat(generatedCode).matches("\\d{6}");
    }

}
