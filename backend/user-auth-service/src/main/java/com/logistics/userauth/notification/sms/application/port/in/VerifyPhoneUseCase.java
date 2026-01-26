package com.logistics.userauth.notification.sms.application.port.in;

import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.sms.application.port.in.command.VerifyPhoneCommand;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.application.usecase.VerifyPhoneService;

/**
 * Входной порт (Inbound Port) use case-а подтверждения телефона через SMS код.
 *
 * <h2>Назначение</h2>
 * Определяет контракт бизнес-сценария: проверить введенный пользователем код,
 * учесть количество попыток и (при успехе) установить статус “phone verified”.
 *
 * <h2>Входные данные</h2>
 * <ul>
 *   <li>{@link VerifyPhoneCommand} — телефон и код, введенный пользователем.</li>
 * </ul>
 *
 * <h2>Ошибки</h2>
 * <ul>
 *   <li>{@link InvalidVerificationCodeException} — код неверный/истёк/превышены попытки.</li>
 * </ul>
 *
 * <h2>Реализация</h2>
 * Реализуется в application/usecase слое (например, сервисом сценария верификации телефона).
 *
 * @see VerifyPhoneService
 * @see SmsRepository
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public interface VerifyPhoneUseCase {

    void verify(VerifyPhoneCommand command) throws Throwable;
}
