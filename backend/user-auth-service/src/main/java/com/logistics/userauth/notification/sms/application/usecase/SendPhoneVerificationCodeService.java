package com.logistics.userauth.notification.sms.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.notification.common.application.exception.NotificationDeliveryException;
import com.logistics.userauth.notification.common.application.usecase.SendAbstractVerificationCodeService;
import com.logistics.userauth.notification.common.domain.VerificationCode;
import com.logistics.userauth.notification.sms.application.exception.SmsDeliveryException;
import com.logistics.userauth.notification.sms.application.port.in.InternalSmsRateLimiterUseCase;
import com.logistics.userauth.notification.sms.application.port.in.SendPhoneVerificationCodeUseCase;
import com.logistics.userauth.notification.sms.application.port.in.command.InternalSmsRateLimiterCommand;
import com.logistics.userauth.notification.sms.application.port.in.command.SendPhoneVerificationCodeCommand;
import com.logistics.userauth.notification.sms.application.port.out.SendSmsPort;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для генерации и отправки SMS кодов верификации.
 *
 * <p>Использует общую логику из {@link com.logistics.userauth.notification.common.application.usecase.SendAbstractVerificationCodeService}.</p>
 *
 * <h2>Алгоритм работы</h2>
 * <ol>
 *   <li>Проверка rate limiting</li>
 *   <li>Генерация криптографически стойкого кода</li>
 *   <li>Сохранение в Redis с TTL</li>
 *   <li>Отправка SMS через провайдер</li>
 *   <li>Логирование результата</li>
 * </ol>
 */
@Service
public class SendPhoneVerificationCodeService extends SendAbstractVerificationCodeService implements SendPhoneVerificationCodeUseCase {

    private final InternalSmsRateLimiterUseCase rateLimiterUseCase;

    @Value("${app.sms.verification.code-length:6}")
    private int codeLength;

    @Value("${app.sms.verification.code-ttl-minutes:5}")
    private long codeTtlMinutes;

    public SendPhoneVerificationCodeService(
            SmsRepository repository,
            SendSmsPort sendPort,
            InternalSmsRateLimiterUseCase rateLimiterUseCase) {
        super(repository, sendPort);
        this.rateLimiterUseCase = rateLimiterUseCase;
    }

    @Override
    protected String normalizeId(String id) {
        return PhoneUtils.normalize(id);
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
        return "SMS";
    }

    @Override
    protected VerificationCode createVerificationCode(String id, String code, LocalDateTime expiresAt) {
        return SmsVerificationCode.builder()
                .id(id)
                .code(code)
                .expiresAt(expiresAt)
                .attempts(0)
                .build();
    }

    @Override
    protected NotificationDeliveryException createDeliveryException(String message) {
        return new SmsDeliveryException(message);
    }

    @Override
    public void sendCode(SendPhoneVerificationCodeCommand command) {
        var rateLimiterCommand = new InternalSmsRateLimiterCommand(command.phone());
        rateLimiterUseCase.checkRateLimiter(rateLimiterCommand);

        sendCode(command.phone());
    }
}
