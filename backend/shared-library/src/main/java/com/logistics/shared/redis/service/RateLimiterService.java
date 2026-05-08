package com.logistics.shared.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Rate limiting через Redis для защиты от брутфорса.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "logistics.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterService {

    private final RedisService redisService;

    /**
     * Проверяет, не превышен ли лимит запросов.
     *
     * @param identifier Уникальный идентификатор (IP, userId, phone)
     * @param maxAttempts Максимум попыток
     * @param window Временное окно
     * @return true если лимит превышен
     */
    public boolean isRateLimited(String identifier, int maxAttempts, Duration window) {
        String key = "rate-limit:" + identifier;
        Long attempts = redisService.incrementWithTtl(key, window);
        return attempts != null && attempts > maxAttempts;
    }

    /**
     * Сбросить счетчик попыток.
     */
    public void reset(String identifier) {
        redisService.delete("rate-limit:" + identifier);
    }
}
