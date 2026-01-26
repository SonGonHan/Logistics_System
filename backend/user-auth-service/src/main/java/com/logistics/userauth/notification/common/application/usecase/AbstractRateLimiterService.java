package com.logistics.userauth.notification.common.application.usecase;

import com.logistics.shared.redis.service.RateLimiterService;
import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Абстрактный сервис для проверки rate limit на отправку уведомлений.
 *
 * <p>Использует {@link RateLimiterService} для ограничения частоты запросов
 * и защиты от спама через различные каналы (SMS, Email и т.д.).</p>
 *
 * <h2>Механизм работы</h2>
 * <ul>
 *   <li>Проверяет количество запросов в заданный период времени</li>
 *   <li>Блокирует повторные отправки раньше cooldown периода</li>
 *   <li>Использует Redis для хранения счётчиков</li>
 * </ul>
 *
 * <p>Подклассы должны определить:</p>
 * <ul>
 *   <li>Префикс идентификатора в Redis (например, "sms:send:", "email:send:")</li>
 *   <li>Метод нормализации идентификатора</li>
 *   <li>Cooldown период и максимальное количество попыток</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRateLimiterService {

    protected final RateLimiterService rateLimiterService;

    protected abstract String getIdentifierPrefix();

    protected abstract String normalizeId(String id);

    protected abstract long getResendCooldownSeconds();

    protected abstract int getMaxAttempts();

    protected abstract String getChannelName();

    protected void checkRateLimiter(String id) {
        String normalizedId = normalizeId(id);
        String identifier = getIdentifierPrefix() + normalizedId;

        if (rateLimiterService.isRateLimited(identifier, getMaxAttempts(), Duration.ofSeconds(getResendCooldownSeconds()))) {
            log.warn("Rate limit exceeded for {}: {}", getChannelName(), normalizedId);
            throw new RateLimitExceededException(
                    String.format("Повторная отправка возможна через %d секунд", getResendCooldownSeconds())
            );
        }
    }
}