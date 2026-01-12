package com.logistics.userauth.sms.application.port.out;

import com.logistics.userauth.sms.domain.SmsVerificationCode;

import java.util.Optional;

/**
 * Репозиторий для управления SMS верификацией.
 *
 * <p>Предоставляет операции для:</p>
 * <ul>
 *   <li>Хранения и получения кодов верификации (временное хранилище)</li>
 *   <li>Управления статусом верификации телефонов</li>
 * </ul>
 *
 * <p>Реализация определяет конкретное хранилище и механизмы.</p>
 *
 * @see com.logistics.userauth.sms.adapter.out.persistence.SmsRepositoryPersistenceAdapter
 */
public interface SmsRepository {
    /**
     * Сохраняет код верификации во временное хранилище.
     *
     * @param code код верификации с метаданными
     * @param ttlMinutes время жизни кода в минутах
     */
    void save(SmsVerificationCode code, long ttlMinutes);
    Optional<SmsVerificationCode> findByPhone(String phone);
    void deleteVerificationCode(String phone);
    void deleteVerificationStatus(String phone);
    void incrementAttempts(String phone);
    void markPhoneAsVerified(String phone, long ttlMinutes);
    boolean isPhoneVerified(String phone);

}
