package com.logistics.userauth.notification.email.adapter.out.persistence;

import com.logistics.shared.redis.service.RedisService;
import com.logistics.userauth.notification.common.adapter.out.persistence.AbstractRedisVerificationRepository;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Redis-реализация хранилища email верификации.
 *
 * <h2>Структура ключей в Redis</h2>
 * <ul>
 *   <li><b>Verification Code:</b> {@code email:verification:{email}}</li>
 *   <li><b>Verification Status:</b> {@code email:verified:{email}}</li>
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
 *   "id": "user@example.com",
 *   "code": "123456",
 *   "expiresAt": "2025-12-20T16:35:00",
 *   "attempts": 0
 * }
 * </pre>
 *
 * <p>Статус верификации хранится как строка "verified".</p>
 *
 * @see EmailPersistenceAdapter
 */
@Repository
public class RedisEmailRepository extends AbstractRedisVerificationRepository<EmailVerificationCode> {

    @Value("${logistics.redis.email-code.ttl-minutes:5}")
    private long defaultTtlMinutes;

    public RedisEmailRepository(RedisService redisService) {
        super("email:verification:",
                "email:verified:",
                redisService);
    }

    @Override
    protected Class<EmailVerificationCode> getCodeClass() {
        return EmailVerificationCode.class;
    }

    @Override
    protected long getDefaultTtlMinutes() {
        return defaultTtlMinutes;
    }

    @Override
    protected String getChannelName() {
        return "Email";
    }
}