package com.logistics.userauth.notification.common.application.usecase;

import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.common.application.port.out.VerificationCodeRepository;
import com.logistics.userauth.notification.common.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Абстрактный сервис для верификации кодов подтверждения.
 *
 * <p>Содержит общую логику верификации для всех каналов уведомлений (SMS, Email и т.д.).</p>
 *
 * <h2>Алгоритм верификации</h2>
 * <ol>
 *   <li>Нормализация идентификатора (телефон/email)</li>
 *   <li>Поиск кода в хранилище</li>
 *   <li>Проверка срока действия</li>
 *   <li>Сравнение введённого кода с сохранённым</li>
 *   <li>Управление счётчиком попыток</li>
 *   <li>Пометка идентификатора как верифицированного</li>
 * </ol>
 *
 * <p>Подклассы должны определить:</p>
 * <ul>
 *   <li>Метод нормализации идентификатора</li>
 *   <li>Максимальное количество попыток</li>
 *   <li>TTL статуса верификации</li>
 *   <li>Репозиторий для работы с кодами</li>
 * </ul>
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractVerifyService<T extends VerificationCodeRepository> {

    protected final T repository;

    protected abstract String normalizeId(String id);

    protected abstract int getMaxAttempts();

    protected abstract long getVerifiedStatusTtlMinutes();

    /**
     * Возвращает имя канала для логирования.
     *
     * @return имя канала
     */
    protected abstract String getChannelName();

    protected void verifyCode(String id, String code) throws Throwable {
        var normalizedId = normalizeId(id);

        log.info("Verifying {}: {}", getChannelName(), normalizedId);

        var storedCode = (VerificationCode) repository.findById(normalizedId)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "Код не найден. Запросите новый код."
                ));

        if (storedCode.isExpired()) {
            repository.deleteVerificationCode(normalizedId);
            log.warn("Verification code expired for {}: {}", getChannelName(), normalizedId);
            throw new InvalidVerificationCodeException(
                    "Срок действия кода истек. Запросите новый код."
            );
        }

        if (!storedCode.getCode().equals(code)) {
            int newAttempts = storedCode.getAttempts() + 1;

            repository.incrementAttempts(normalizedId);

            if (newAttempts >= getMaxAttempts()) {
                repository.deleteVerificationCode(normalizedId);
                log.warn("Max attempts reached for {}: {}", getChannelName(), normalizedId);
                throw new InvalidVerificationCodeException("Неверный код. Превышено количество попыток.");
            }

            int remainingAttempts = getMaxAttempts() - newAttempts;
            log.warn("Invalid code for {}: {}. Remaining attempts: {}", getChannelName(), normalizedId, remainingAttempts);
            throw new InvalidVerificationCodeException(
                    String.format("Неверный код. Осталось попыток: %d", remainingAttempts)
            );
        }

        repository.deleteVerificationCode(normalizedId);
        repository.markAsVerified(normalizedId, getVerifiedStatusTtlMinutes());

        log.info("{} verified successfully: {}", getChannelName(), normalizedId);
    }
}