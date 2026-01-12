package com.logistics.userauth.sms.application.port.in.command;

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
 * @see com.logistics.userauth.sms.application.port.in.SendVerificationCodeUseCase
 * @see com.logistics.userauth.sms.adapter.in.web.dto.SendVerificationCodeRequest
 */
public record SendVerificationCodeCommand(String phone) {
}
