package com.logistics.userauth.notification.sms.application.port.in;

import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import com.logistics.userauth.notification.sms.application.exception.SmsDeliveryException;
import com.logistics.userauth.notification.sms.application.port.in.command.SendPhoneVerificationCodeCommand;
import com.logistics.userauth.notification.sms.application.port.out.SendSmsPort;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.application.usecase.SendPhoneVerificationCodeService;

/**
 * Входной порт (Inbound Port) use case-а отправки SMS кода верификации.
 *
 * <h2>Назначение</h2>
 * Определяет контракт бизнес-сценария: сгенерировать одноразовый код, сохранить его с TTL
 * и отправить пользователю через SMS провайдера.
 *
 * <h2>Кто вызывает</h2>
 * Обычно вызывается из web-адаптера (REST контроллера), который принимает номер телефона
 * и формирует {@link SendPhoneVerificationCodeCommand}.
 *
 * <h2>Входные данные</h2>
 * <ul>
 *   <li>{@link SendPhoneVerificationCodeCommand} — содержит телефон, на который нужно отправить код.</li>
 * </ul>
 *
 * <h2>Ошибки</h2>
 * <ul>
 *   <li>{@link RateLimitExceededException} — превышен лимит отправки.</li>
 *   <li>{@link SmsDeliveryException} — ошибка отправки SMS провайдером.</li>
 * </ul>
 *
 * <h2>Реализация</h2>
 * Реализуется в application/usecase слое (например, сервисом сценария отправки кода).
 *
 * @see SendPhoneVerificationCodeService
 * @see SendSmsPort
 * @see SmsRepository
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public interface SendPhoneVerificationCodeUseCase {
    
    void sendCode(SendPhoneVerificationCodeCommand command);
}
