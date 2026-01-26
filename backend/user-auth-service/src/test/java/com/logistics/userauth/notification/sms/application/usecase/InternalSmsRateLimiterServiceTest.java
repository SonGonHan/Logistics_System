package com.logistics.userauth.notification.sms.application.usecase;

import com.logistics.shared.redis.service.RateLimiterService;
import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import com.logistics.userauth.notification.sms.application.port.in.command.InternalSmsRateLimiterCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InternalSmsRateLimiterService - тестирование ограничения SMS запросов")
class InternalSmsRateLimiterServiceTest {

    @Mock
    private RateLimiterService rateLimiterService;

    private InternalSmsRateLimiterService service;

    @BeforeEach
    void setUp() {
        service = new InternalSmsRateLimiterService(rateLimiterService);

        ReflectionTestUtils.setField(service, "resendCooldownSeconds", 60L);
        ReflectionTestUtils.setField(service, "maxAttempts", 1);
    }

    @Test
    @DisplayName("Должен разрешить запрос если лимит не превышен")
    void shouldAllowRequestWhenNotRateLimited() {
        // Given
        String phone = "89991234567";
        var command = new InternalSmsRateLimiterCommand(phone);

        when(rateLimiterService.isRateLimited(anyString(), eq(1), any(Duration.class)))
                .thenReturn(false);

        // When & Then
        assertThatCode(() -> service.checkRateLimiter(command))
                .doesNotThrowAnyException();

        verify(rateLimiterService).isRateLimited(
                eq("sms:send:" + phone),
                eq(1),
                eq(Duration.ofSeconds(60))
        );
    }

    @Test
    @DisplayName("Должен бросить исключение если лимит превышен")
    void shouldThrowExceptionWhenRateLimited() {
        // Given
        String phone = "89991234567";
        var command = new InternalSmsRateLimiterCommand(phone);

        when(rateLimiterService.isRateLimited(anyString(), eq(1), any(Duration.class)))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> service.checkRateLimiter(command))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("60");
    }
}
