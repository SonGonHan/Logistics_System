package com.logistics.userauth.sms.application.port.in;

import com.logistics.userauth.sms.application.port.in.command.SendVerificationCodeCommand;

/**
 * Входной порт (Inbound Port) use case-а отправки SMS кода верификации.
 *
 * <h2>Назначение</h2>
 * Определяет контракт бизнес-сценария: сгенерировать одноразовый код, сохранить его с TTL
 * и отправить пользователю через SMS провайдера.
 *
 * <h2>Кто вызывает</h2>
 * Обычно вызывается из web-адаптера (REST контроллера), который принимает номер телефона
 * и формирует {@link SendVerificationCodeCommand}.
 *
 * <h2>Входные данные</h2>
 * <ul>
 *   <li>{@link SendVerificationCodeCommand} — содержит телефон, на который нужно отправить код.</li>
 * </ul>
 *
 * <h2>Ошибки</h2>
 * <ul>
 *   <li>{@link com.logistics.userauth.sms.application.exception.RateLimitExceededException} — превышен лимит отправки.</li>
 *   <li>{@link com.logistics.userauth.sms.application.exception.SmsDeliveryException} — ошибка отправки SMS провайдером.</li>
 * </ul>
 *
 * <h2>Реализация</h2>
 * Реализуется в application/usecase слое (например, сервисом сценария отправки кода).
 *
 * @see com.logistics.userauth.sms.application.usecase.SendVerificationCodeService
 * @see com.logistics.userauth.sms.application.port.out.SendSmsPort
 * @see com.logistics.userauth.sms.application.port.out.SmsRepository
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public interface SendVerificationCodeUseCase {
    
    void sendCode(SendVerificationCodeCommand command);
}
