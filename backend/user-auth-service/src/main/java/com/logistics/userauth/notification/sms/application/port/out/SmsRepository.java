package com.logistics.userauth.notification.sms.application.port.out;

import com.logistics.userauth.notification.common.application.port.out.VerificationCodeRepository;
import com.logistics.userauth.notification.sms.adapter.out.persistence.SmsPersistenceAdapter;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

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
 * @see SmsPersistenceAdapter
 */
public interface SmsRepository extends VerificationCodeRepository<SmsVerificationCode> {

}
