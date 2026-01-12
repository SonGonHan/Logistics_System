package com.logistics.shared.redis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimiterService: тестирование ограничения частоты запросов")
class RateLimiterServiceTest {

    @Mock
    private RedisService redisService;

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService(redisService);
    }

    @Test
    @DisplayName("Должен разрешить первый запрос")
    void shouldAllowFirstRequest() {
        // Given
        String identifier = "user123";
        int maxAttempts = 3;
        Duration window = Duration.ofMinutes(1);
        when(redisService.incrementWithTtl(anyString(), any(Duration.class))).thenReturn(1L);

        // When
        boolean isLimited = rateLimiterService.isRateLimited(identifier, maxAttempts, window);

        // Then
        assertThat(isLimited).isFalse();
        verify(redisService).incrementWithTtl(eq("rate-limit:" + identifier), eq(window));
    }

    @Test
    @DisplayName("Должен разрешить запросы в пределах лимита")
    void shouldAllowRequestsWithinLimit() {
        // Given
        String identifier = "user123";
        int maxAttempts = 3;
        Duration window = Duration.ofMinutes(1);
        when(redisService.incrementWithTtl(anyString(), any(Duration.class))).thenReturn(2L);

        // When
        boolean isLimited = rateLimiterService.isRateLimited(identifier, maxAttempts, window);

        // Then
        assertThat(isLimited).isFalse();
    }

    @Test
    @DisplayName("Должен заблокировать запросы при превышении лимита")
    void shouldBlockRequestsWhenLimitExceeded() {
        // Given
        String identifier = "user123";
        int maxAttempts = 3;
        Duration window = Duration.ofMinutes(1);
        when(redisService.incrementWithTtl(anyString(), any(Duration.class))).thenReturn(4L);

        // When
        boolean isLimited = rateLimiterService.isRateLimited(identifier, maxAttempts, window);

        // Then
        assertThat(isLimited).isTrue();
    }

    @Test
    @DisplayName("Должен заблокировать при достижении точного лимита")
    void shouldBlockWhenExactlyAtLimit() {
        // Given
        String identifier = "user123";
        int maxAttempts = 1;
        Duration window = Duration.ofMinutes(1);
        when(redisService.incrementWithTtl(anyString(), any(Duration.class))).thenReturn(3L);

        // When
        boolean isLimited = rateLimiterService.isRateLimited(identifier, maxAttempts, window);

        // Then
        assertThat(isLimited).isTrue();
    }

    @Test
    @DisplayName("Должен обработать null ответ от Redis")
    void shouldHandleNullResponseFromRedis() {
        // Given
        String identifier = "user123";
        int maxAttempts = 3;
        Duration window = Duration.ofMinutes(1);
        when(redisService.incrementWithTtl(anyString(), any(Duration.class))).thenReturn(null);

        // When
        boolean isLimited = rateLimiterService.isRateLimited(identifier, maxAttempts, window);

        // Then
        assertThat(isLimited).isFalse();
    }

    @Test
    @DisplayName("Должен сбросить счетчик rate limit")
    void shouldResetRateLimit() {
        // Given
        String identifier = "user123";

        // When
        rateLimiterService.reset(identifier);

        // Then
        verify(redisService).delete("rate-limit:" + identifier);
    }
}
