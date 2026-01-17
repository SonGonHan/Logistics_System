package com.logistics.userauth.sms.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.sms.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.sms.application.port.in.VerifyPhoneUseCase;
import com.logistics.userauth.sms.application.port.in.command.VerifyPhoneCommand;
import com.logistics.userauth.sms.application.port.out.SmsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Сервис для верификации телефонов через SMS коды.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyPhoneService implements VerifyPhoneUseCase {

    private final SmsRepository repository;

    @Value("${app.sms.verification.max-attempts}")
    private int maxAttempts;

    @Value("${app.sms.verification.verified-status-ttl-minutes:10}")
    private long verifiedStatusTtlMinutes;


    /**
     * Проверяет введенный пользователем код.
     */
    @Override
    public void verify(VerifyPhoneCommand command) {
        var normalizedPhone = PhoneUtils.normalize(command.phone());

        log.info("Verifying phone: {}", normalizedPhone);

        var storedCode = repository.findByPhone(normalizedPhone)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "Код не найден. Запросите новый код."
                ));

        if (storedCode.isExpired()) {
            repository.deleteVerificationCode(normalizedPhone);
            log.warn("Verification code expired for phone: {}", normalizedPhone);
            throw new InvalidVerificationCodeException(
                    "Срок действия кода истек. Запросите новый код."
            );
        }

        if (!storedCode.getCode().equals(command.code())) {
            int newAttempts = storedCode.getAttempts() + 1;

            repository.incrementAttempts(normalizedPhone);

            if (newAttempts >= maxAttempts) {
                repository.deleteVerificationCode(normalizedPhone);
                log.warn("Max attempts reached for phone: {}", normalizedPhone);
                throw new InvalidVerificationCodeException("Неверный код. Превышено количество попыток.");
            }

            int remainingAttempts = maxAttempts - newAttempts;
            log.warn("Invalid code for phone: {}. Remaining attempts: {}", normalizedPhone, remainingAttempts);
            throw new InvalidVerificationCodeException(
                    String.format("Неверный код. Осталось попыток: %d", remainingAttempts)
            );
        }


        repository.deleteVerificationCode(normalizedPhone);
        repository.markPhoneAsVerified(normalizedPhone, verifiedStatusTtlMinutes);

        log.info("Phone verified successfully: {}", normalizedPhone);
    }
}
