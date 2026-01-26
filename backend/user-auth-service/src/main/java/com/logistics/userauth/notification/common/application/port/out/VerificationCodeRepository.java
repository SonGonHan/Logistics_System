package com.logistics.userauth.notification.common.application.port.out;

import com.logistics.userauth.notification.common.domain.VerificationCode;

import java.util.Optional;

/**
 * Выходной порт для работы с хранилищем кодов верификации.
 *
 * <p>Обеспечивает персистентность кодов верификации в Redis
 * с поддержкой TTL и управления попытками ввода.</p>
 *
 * <h2>Реализации</h2>
 * <ul>
 *   <li>SMS - {@link com.logistics.userauth.notification.sms.adapter.out.persistence.RedisSmsRepository}</li>
 *   <li>Email - {@link com.logistics.userauth.notification.email.adapter.out.persistence.RedisEmailRepository}</li>
 * </ul>
 */
public interface VerificationCodeRepository<T extends VerificationCode> {

    void save(T verificationCode, long ttlMinutes);

    Optional <T> findById(String id);

    void deleteVerificationCode(String id);

    void deleteVerificationStatus(String id);

    void incrementAttempts(String id);

    void markAsVerified(String id, long ttlMinutes);
    
    boolean isVerified(String id);
}