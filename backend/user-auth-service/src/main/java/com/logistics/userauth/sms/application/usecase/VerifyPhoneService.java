package com.logistics.userauth.sms.application.usecase;

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
        log.info("Verifying phone: {}", command.phone());

        var storedCode = repository.findByPhone(command.phone())
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "Код не найден. Запросите новый код."
                ));

        if (storedCode.isExpired()) {
            repository.deleteVerificationCode(command.phone());
            log.warn("Verification code expired for phone: {}", command.phone());
            throw new InvalidVerificationCodeException(
                    "Срок действия кода истек. Запросите новый код."
            );
        }

        if (!storedCode.getCode().equals(command.code())) {
            int newAttempts = storedCode.getAttempts() + 1;

            repository.incrementAttempts(command.phone());

            if (newAttempts >= maxAttempts) {
                repository.deleteVerificationCode(command.phone());
                log.warn("Max attempts reached for phone: {}", command.phone());
                throw new InvalidVerificationCodeException("Неверный код. Превышено количество попыток.");
            }

            int remainingAttempts = maxAttempts - newAttempts;
            log.warn("Invalid code for phone: {}. Remaining attempts: {}", command.phone(), remainingAttempts);
            throw new InvalidVerificationCodeException(
                    String.format("Неверный код. Осталось попыток: %d", remainingAttempts)
            );
        }


        repository.deleteVerificationCode(command.phone());
        repository.markPhoneAsVerified(command.phone(), verifiedStatusTtlMinutes);

        log.info("Phone verified successfully: {}", command.phone());
    }
}
