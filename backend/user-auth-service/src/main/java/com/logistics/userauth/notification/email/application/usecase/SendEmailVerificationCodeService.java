package com.logistics.userauth.notification.email.application.usecase;

import com.logistics.userauth.notification.common.application.exception.NotificationDeliveryException;
import com.logistics.userauth.notification.common.application.usecase.SendAbstractVerificationCodeService;
import com.logistics.userauth.notification.common.domain.VerificationCode;
import com.logistics.userauth.notification.email.application.exception.EmailDeliveryException;
import com.logistics.userauth.notification.email.application.port.in.InternalEmailRateLimiterUseCase;
import com.logistics.userauth.notification.email.application.port.in.SendEmailVerificationCodeUseCase;
import com.logistics.userauth.notification.email.application.port.in.command.InternalEmailRateLimiterCommand;
import com.logistics.userauth.notification.email.application.port.in.command.SendEmailVerificationCodeCommand;
import com.logistics.userauth.notification.email.application.port.out.EmailRepository;
import com.logistics.userauth.notification.email.application.port.out.SendEmailPort;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для генерации и отправки кодов верификации на email.
 *
 * <p>Использует общую логику из {@link SendAbstractVerificationCodeService}.</p>
 *
 * <h2>Алгоритм работы</h2>
 * <ol>
 *   <li>Нормализация email (приведение к нижнему регистру)</li>
 *   <li>Проверка rate limiting</li>
 *   <li>Генерация криптографически стойкого кода</li>
 *   <li>Сохранение в Redis с TTL</li>
 *   <li>Отправка email через провайдер</li>
 * </ol>
 */
@Service
public class SendEmailVerificationCodeService extends SendAbstractVerificationCodeService implements SendEmailVerificationCodeUseCase {

    private final InternalEmailRateLimiterUseCase rateLimiterUseCase;

    @Value("${app.email.verification.code-length:6}")
    private int codeLength;

    @Value("${app.email.verification.code-ttl-minutes:5}")
    private long codeTtlMinutes;

    public SendEmailVerificationCodeService(
            EmailRepository repository,
            SendEmailPort sendPort,
            InternalEmailRateLimiterUseCase rateLimiterUseCase) {
        super(repository, sendPort);
        this.rateLimiterUseCase = rateLimiterUseCase;
    }

    @Override
    protected String normalizeId(String id) {
        return id != null ? id.toLowerCase().trim() : id;
    }

    @Override
    protected int getCodeLength() {
        return codeLength;
    }

    @Override
    protected long getCodeTtlMinutes() {
        return codeTtlMinutes;
    }

    @Override
    protected String getChannelName() {
        return "Email";
    }

    @Override
    protected VerificationCode createVerificationCode(String id, String code, LocalDateTime expiresAt) {
        return EmailVerificationCode.builder()
                .id(id)
                .code(code)
                .expiresAt(expiresAt)
                .attempts(0)
                .build();
    }

    @Override
    protected NotificationDeliveryException createDeliveryException(String message) {
        return new EmailDeliveryException(message);
    }

    @Override
    public void sendCode(SendEmailVerificationCodeCommand command) {
        // Проверка rate limiting перед отправкой
        var rateLimiterCommand = new InternalEmailRateLimiterCommand(command.email());
        rateLimiterUseCase.checkRateLimiter(rateLimiterCommand);

        // Вызов общей логики отправки из AbstractSendVerificationCodeService
        sendCode(command.email());
    }
}
