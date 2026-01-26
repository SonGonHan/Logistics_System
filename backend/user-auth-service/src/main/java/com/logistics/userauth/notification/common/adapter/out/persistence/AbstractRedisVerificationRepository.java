package com.logistics.userauth.notification.common.adapter.out.persistence;

import com.logistics.shared.redis.service.RedisService;
import com.logistics.userauth.notification.common.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Абстрактная Redis-реализация хранилища верификационных кодов.
 *
 * <p>Содержит общую логику для работы с кодами верификации в Redis.
 * Подклассы должны определить:</p>
 * <ul>
 *   <li>Префиксы ключей Redis</li>
 *   <li>Тип конкретного кода верификации</li>
 *   <li>Метод извлечения идентификатора из кода</li>
 * </ul>
 *
 * <h2>Структура ключей в Redis</h2>
 * <ul>
 *   <li><b>Verification Code:</b> {@code {channel}:verification:{id}}</li>
 *   <li><b>Verification Status:</b> {@code {channel}:verified:{id}}</li>
 * </ul>
 *
 * @param <T> тип конкретного кода верификации (SmsVerificationCode, EmailVerificationCode)
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRedisVerificationRepository<T extends VerificationCode> {

    protected final String KEY_PREFIX;
    protected final String VERIFIED_KEY_PREFIX;

    protected final RedisService redisService;

    /**
     * Возвращает класс конкретного типа кода верификации.
     *
     * @return класс кода верификации
     */
    protected abstract Class<T> getCodeClass();

    /**
     * Возвращает TTL по умолчанию из конфигурации.
     *
     * @return TTL в минутах
     */
    protected abstract long getDefaultTtlMinutes();

    /**
     * Извлекает идентификатор из кода верификации.
     *
     * @param code код верификации
     * @return идентификатор
     */
    protected String extractId(T code) {
        return code.getId();
    }

    /**
     * Возвращает имя канала для логирования (sms, email и т.д.).
     *
     * @return имя канала
     */
    protected abstract String getChannelName();

    public void save(T code, long ttlMinutes) {
        String id = extractId(code);
        String key = buildKey(id);
        redisService.set(key, code, Duration.ofMinutes(ttlMinutes));
        log.debug("{} code saved to Redis: id={}", getChannelName(), id);
    }

    public Optional<T> findById(String id) {
        String key = buildKey(id);
        return redisService.get(key, getCodeClass());
    }

    public void deleteVerificationCode(String id) {
        String key = buildKey(id);
        redisService.delete(key);
        log.debug("{} code deleted from Redis: id={}", getChannelName(), id);
    }

    public void deleteVerificationStatus(String id) {
        String key = buildVerifiedKey(id);
        redisService.delete(key);
        log.info("Verification status deleted: id={}", id);
    }

    public void incrementAttempts(String id) {
        findById(id).ifPresent(code -> {
            code.setAttempts(code.getAttempts() + 1);

            long remainingTtl = redisService.getTtl(buildKey(id), TimeUnit.MINUTES)
                    .orElse(getDefaultTtlMinutes());

            save(code, remainingTtl);
        });
    }

    public void markAsVerified(String id, long ttlMinutes) {
        String key = buildVerifiedKey(id);
        redisService.set(key, "verified", Duration.ofMinutes(ttlMinutes));
        log.info("{} marked as verified: id={}, ttl={}min", getChannelName(), id, ttlMinutes);
    }

    public boolean isVerified(String id) {
        String key = buildVerifiedKey(id);
        Optional<String> status = redisService.get(key, String.class);

        boolean verified = status.isPresent();
        log.debug("{} verification status checked: id={}, verified={}", getChannelName(), id, verified);

        return verified;
    }

    protected String buildKey(String id) {
        return KEY_PREFIX + id;
    }

    protected String buildVerifiedKey(String id) {
        return VERIFIED_KEY_PREFIX + id;
    }
}