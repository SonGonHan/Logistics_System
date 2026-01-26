package com.logistics.userauth.notification.sms.adapter.out.persistence;

import com.logistics.shared.redis.service.RedisService;
import com.logistics.userauth.notification.common.adapter.out.persistence.AbstractRedisVerificationRepository;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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
 *   "id": "79991234567",
 *   "code": "123456",
 *   "expiresAt": "2025-12-20T16:35:00",
 *   "attempts": 0
 * }
 * </pre>
 *
 * <p>Статус верификации хранится как строка "verified".</p>
 *
 * @see SmsPersistenceAdapter
 */
@Repository
public class RedisSmsRepository extends AbstractRedisVerificationRepository<SmsVerificationCode> {

    @Value("${logistics.redis.sms-code.ttl-minutes:5}")
    private long defaultTtlMinutes;

    public RedisSmsRepository(RedisService redisService) {
        super("sms:verification:",
                "sms:verified:",
                redisService);
    }
    @Override
    protected Class<SmsVerificationCode> getCodeClass() {
        return SmsVerificationCode.class;
    }

    @Override
    protected long getDefaultTtlMinutes() {
        return defaultTtlMinutes;
    }

    @Override
    protected String getChannelName() {
        return "SMS";
    }
}
