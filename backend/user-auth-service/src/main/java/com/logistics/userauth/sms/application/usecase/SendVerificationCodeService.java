package com.logistics.userauth.sms.application.usecase;

import com.logistics.shared.redis.service.RateLimiterService;
import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.sms.application.exception.RateLimitExceededException;
import com.logistics.userauth.sms.application.exception.SmsDeliveryException;
import com.logistics.userauth.sms.application.port.in.InternalSmsRateLimiterUseCase;
import com.logistics.userauth.sms.application.port.in.SendVerificationCodeUseCase;
import com.logistics.userauth.sms.application.port.in.command.InternalSmsRateLimiterCommand;
import com.logistics.userauth.sms.application.port.in.command.SendVerificationCodeCommand;
import com.logistics.userauth.sms.application.port.out.SendSmsPort;
import com.logistics.userauth.sms.application.port.out.SmsRepository;
import com.logistics.userauth.sms.domain.SmsVerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Сервис для генерации и отправки SMS кодов верификации.
 *
 * <h2>Алгоритм работы</h2>
 * <ol>
 *   <li>Проверка rate limiting (опционально, если включено)</li>
 *   <li>Генерация криптографически стойкого кода</li>
 *   <li>Сохранение в Redis с TTL</li>
 *   <li>Отправка SMS через провайдер</li>
 *   <li>Логирование результата</li>
 * </ol>
 *
 * <h2>Безопасность</h2>
 * <ul>
 *   <li>Используется {@link SecureRandom} для генерации кодов</li>
 *   <li>Rate limiting защищает от спама</li>
 *   <li>Коды хранятся с TTL для автоматического удаления</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendVerificationCodeService implements SendVerificationCodeUseCase {

    private final SmsRepository smsRepository;
    private final SendSmsPort sendSmsPort;
    private final InternalSmsRateLimiterUseCase internalSmsRateLimiterUseCase;

    @Value("${app.sms.verification.code-length:6}")
    private int codeLength;

    @Value("${app.sms.verification.code-ttl-minutes:5}")
    private long codeTtlMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Отправляет SMS код на указанный номер.
     *
     * @param command Команда отправки кода (phone).
     * @throws RateLimitExceededException если не прошло 60 секунд с последней отправки
     * @throws SmsDeliveryException если провайдер не смог доставить SMS
     */
    @Override
    public void sendCode(SendVerificationCodeCommand command) {
        String normalizedPhone = PhoneUtils.normalize(command.phone());

        log.info("Sending verification code to phone: {}", normalizedPhone);

        var smsRateLimiterCommand = new InternalSmsRateLimiterCommand(normalizedPhone);

        internalSmsRateLimiterUseCase.checkRateLimiter(smsRateLimiterCommand);

//        ensureNoActiveCode(normalizedPhone);

        var code = generateSecureCode();

        var verificationCode = SmsVerificationCode.builder()
                .phone(normalizedPhone)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(codeTtlMinutes))
                .attempts(0)
                .build();

        smsRepository.save(verificationCode, codeTtlMinutes);
        log.debug("Verification code saved to repository: phone={}", normalizedPhone);

        var isSent = sendSmsPort.sendVerificationCode(normalizedPhone, code);

        if (!isSent) {
            log.error("Failed to send SMS to phone: {}", normalizedPhone);
            throw new SmsDeliveryException(
                    "Не удалось отправить SMS. Попробуйте позже или обратитесь в поддержку."
            );
        }

        log.info("Verification code sent successfully: phone={}", normalizedPhone);
    }

    /**
     * Генерирует криптографически стойкий код заданной длины.
     *
     * @return Строка из цифр (например, "123456")
     */
    private String generateSecureCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(secureRandom.nextInt(10));
        }
        log.debug("Generated verification code: {}", code);
        return code.toString();
    }



    /**
     * Проверяет нет ли существующего действительного кода.
     *
     * @param phone Номер телефона
     * @throws RateLimitExceededException если существующий код еще действителен
     */
    private void ensureNoActiveCode(String phone) {
        smsRepository.findByPhone(phone).ifPresent(existingCode -> {
            if (!existingCode.isExpired()) {
                long secondsRemaining = java.time.Duration.between(
                        LocalDateTime.now(),
                        existingCode.getExpiresAt()
                ).getSeconds();

                log.warn("Existing code not expired for phone: {}. Remaining {}s", phone, secondsRemaining);
                throw new RateLimitExceededException(
                        String.format("Существующий код еще действителен. Отправка нового возможна через %d сек.", secondsRemaining)
                );
            }
        });
    }

}
