package com.logistics.userauth.sms.application.port.in;

import com.logistics.userauth.sms.application.port.in.command.VerifyPhoneCommand;

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
 *   <li>{@link com.logistics.userauth.sms.application.exception.InvalidVerificationCodeException} — код неверный/истёк/превышены попытки.</li>
 * </ul>
 *
 * <h2>Реализация</h2>
 * Реализуется в application/usecase слое (например, сервисом сценария верификации телефона).
 *
 * @see com.logistics.userauth.sms.application.usecase.VerifyPhoneService
 * @see com.logistics.userauth.sms.application.port.out.SmsRepository
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public interface VerifyPhoneUseCase {

    void verify(VerifyPhoneCommand command);
}
