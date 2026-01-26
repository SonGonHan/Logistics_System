package com.logistics.userauth.notification.sms.application.port.in.command;

import com.logistics.userauth.notification.sms.adapter.in.web.dto.PhoneVerificationCodeRequest;
import com.logistics.userauth.notification.sms.application.port.in.SendPhoneVerificationCodeUseCase;

/**
 * Команда для use case-а отправки SMS кода верификации.
 *
 * <h2>Назначение</h2>
 * Содержит входные параметры, необходимые для запуска сценария генерации и отправки кода на телефон.
 *
 * <h2>Поля</h2>
 * <ul>
 *   <li><b>phone</b> — номер телефона получателя в формате, поддерживаемом валидацией на web-слое.</li>
 * </ul>
 *
 * <h2>Пример</h2>
 * <pre>{@code
 * var command = new SendVerificationCodeCommand("79991234567");
 * sendVerificationCodeUseCase.sendCode(command);
 * }</pre>
 *
 * @see SendPhoneVerificationCodeUseCase
 * @see PhoneVerificationCodeRequest
 */
public record SendPhoneVerificationCodeCommand(String phone) {
}
