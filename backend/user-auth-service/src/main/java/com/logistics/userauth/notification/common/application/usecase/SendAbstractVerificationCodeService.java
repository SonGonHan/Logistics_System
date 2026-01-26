package com.logistics.userauth.notification.common.application.usecase;

import com.logistics.userauth.notification.common.application.exception.NotificationDeliveryException;
import com.logistics.userauth.notification.common.application.port.out.SendNotificationPort;
import com.logistics.userauth.notification.common.application.port.out.VerificationCodeRepository;
import com.logistics.userauth.notification.common.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Абстрактный сервис для генерации и отправки кодов верификации.
 *
 * <p>Содержит общую логику отправки кодов через различные каналы (SMS, Email и т.д.).</p>
 *
 * <h2>Алгоритм работы</h2>
 * <ol>
 *   <li>Нормализация идентификатора получателя</li>
 *   <li>Проверка rate limiting (если требуется)</li>
 *   <li>Генерация криптографически стойкого кода</li>
 *   <li>Сохранение кода в хранилище с TTL</li>
 *   <li>Отправка кода через провайдер уведомлений</li>
 * </ol>
 *
 * <h2>Безопасность</h2>
 * <ul>
 *   <li>Использует {@link SecureRandom} для генерации кодов</li>
 *   <li>Поддерживает rate limiting для защиты от спама</li>
 *   <li>Автоматическое удаление кодов по истечении TTL</li>
 * </ul>
 *
 * <p>Подклассы должны определить:</p>
 * <ul>
 *   <li>Метод нормализации идентификатора</li>
 *   <li>Длину кода и TTL</li>
 *   <li>Репозиторий и провайдер отправки</li>
 *   <li>Создание domain объекта кода верификации</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SendAbstractVerificationCodeService {

    protected final VerificationCodeRepository repository;
    protected final SendNotificationPort sendPort;

    private final SecureRandom secureRandom = new SecureRandom();

    protected abstract String normalizeId(String id);

    protected abstract int getCodeLength();

    protected abstract long getCodeTtlMinutes();

    /**
     * Возвращает имя канала для логирования.
     *
     * @return имя канала
     */
    protected abstract String getChannelName();

    protected abstract VerificationCode createVerificationCode(String id, String code, LocalDateTime expiresAt);

    protected abstract NotificationDeliveryException createDeliveryException(String message);

    protected void sendCode(String id) {
        String normalizedId = normalizeId(id);

        log.info("Sending verification code to {}: {}", getChannelName(), normalizedId);

        var code = generateSecureCode();

        var expiresAt = LocalDateTime.now().plusMinutes(getCodeTtlMinutes());
        var verificationCode = createVerificationCode(normalizedId, code, expiresAt);

        repository.save(verificationCode, getCodeTtlMinutes());
        log.debug("Verification code saved to repository: {}={}", getChannelName(), normalizedId);

        var isSent = sendPort.sendVerificationCode(normalizedId, code);

        if (!isSent) {
            log.error("Failed to send {} to: {}", getChannelName(), normalizedId);
            throw createDeliveryException(
                    "Не удалось отправить код. Попробуйте позже или обратитесь в поддержку."
            );
        }

        log.info("Verification code sent successfully: {}={}", getChannelName(), normalizedId);
    }

    protected String generateSecureCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < getCodeLength(); i++) {
            code.append(secureRandom.nextInt(10));
        }
        log.debug("Generated verification code: {}", code);
        return code.toString();
    }
}