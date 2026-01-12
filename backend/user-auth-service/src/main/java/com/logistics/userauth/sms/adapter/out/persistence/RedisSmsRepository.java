package com.logistics.userauth.sms.adapter.out.persistence;

import com.logistics.shared.redis.service.RedisService;
import com.logistics.userauth.sms.domain.SmsVerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis-реализация хранилища SMS верификации.
 *
 * <h2>Структура ключей в Redis</h2>
 * <ul>
 *   <li><b>Verification Code:</b> {@code sms:verification:{phone}}</li>
 *   <li><b>Verification Status:</b> {@code sms:verified:{phone}}</li>
 * </ul>
 *
 * <h2>TTL (Time To Live)</h2>
 * <ul>
 *   <li>Код верификации: 5 минут (конфигурируемо)</li>
 *   <li>Статус верификации: 10 минут (конфигурируемо)</li>
 * </ul>
 *
 * <h2>Формат данных</h2>
 * <p>Код верификации хранится как JSON:</p>
 * <pre>
 * {
 *   "phone": "79991234567",
 *   "code": "123456",
 *   "expiresAt": "2025-12-20T16:35:00",
 *   "attempts": 0
 * }
 * </pre>
 *
 * <p>Статус верификации хранится как строка "verified".</p>
 *
 * @see SmsRepositoryPersistenceAdapter
 */
@Slf4j
@Repository
@RequiredArgsConstructor
class RedisSmsRepository {

    private static final String KEY_PREFIX = "sms:verification:";
    private static final String VERIFIED_KEY_PREFIX = "sms:verified:";

    private final RedisService redisService;

    @Value("${logistics.redis.sms-code.ttl-minutes:5}")
    private long defaultTtlMinutes;

    void save(SmsVerificationCode code, long ttlMinutes) {
        String key = buildKey(code.getPhone());
        redisService.set(key, code, Duration.ofMinutes(ttlMinutes));
        log.debug("SMS code saved to Redis: phone={}", code.getPhone());
    }

    Optional<SmsVerificationCode> findByPhone(String phone) {
        String key = buildKey(phone);
        return redisService.get(key, SmsVerificationCode.class);
    }

    void deleteVerificationCode(String phone) {
        String key = buildKey(phone);
        redisService.delete(key);
        log.debug("SMS code deleted from Redis: phone={}", phone);
    }

    void incrementAttempts(String phone) {
        findByPhone(phone).ifPresent(code -> {
            code.setAttempts(code.getAttempts() + 1);

            long remainingTtl = redisService.getTtl(buildKey(phone), TimeUnit.MINUTES)
                    .orElse(defaultTtlMinutes);

            save(code, remainingTtl);
        });
    }

    public void markPhoneAsVerified(String phone, long ttlMinutes) {
        String key = buildVerifiedKey(phone);
        redisService.set(key, "verified", Duration.ofMinutes(ttlMinutes));
        log.info("Phone marked as verified: phone={}, ttl={}min", phone, ttlMinutes);

    }

    public boolean isPhoneVerified(String phone) {
        String key = buildVerifiedKey(phone);
        Optional<String> status = redisService.get(key, String.class);

        boolean verified = status.isPresent();
        log.debug("Phone verification status checked: phone={}, verified={}", phone, verified);

        return verified;
    }

    public void deleteVerificationStatus(String phone) {
        String key = buildVerifiedKey(phone);  // "sms:verified:79991234567"
        redisService.delete(key);
        log.info("Verification status deleted: phone={}", phone);
    }

    private String buildKey(String phone) {
        return KEY_PREFIX + phone;
    }
    private String buildVerifiedKey(String phone) {
        return VERIFIED_KEY_PREFIX + phone;
    }


}
