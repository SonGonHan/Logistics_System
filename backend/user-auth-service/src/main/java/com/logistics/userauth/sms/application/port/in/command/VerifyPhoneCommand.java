package com.logistics.userauth.sms.application.port.in.command;

import lombok.Builder;

/**
 * Команда для use case-а подтверждения телефона по SMS коду.
 *
 * <h2>Назначение</h2>
 * Передаёт в application-слой пару (телефон + код), введенную пользователем, для проверки,
 * учета попыток и установки статуса “verified” при успешной верификации.
 *
 * <h2>Поля</h2>
 * <ul>
 *   <li><b>phone</b> — номер телефона, для которого выполняется подтверждение.</li>
 *   <li><b>code</b> — одноразовый код верификации (обычно 6 цифр).</li>
 * </ul>
 *
 * <h2>Пример</h2>
 * <pre>{@code
 * var command = VerifyPhoneCommand.builder()
 *     .phone("79991234567")
 *     .code("123456")
 *     .build();
 *
 * verifyPhoneUseCase.verify(command);
 * }</pre>
 *
 * @see com.logistics.userauth.sms.application.port.in.VerifyPhoneUseCase
 * @see com.logistics.userauth.sms.adapter.in.web.dto.VerifyPhoneRequest
 */
@Builder
public record VerifyPhoneCommand(
        String phone,
        String code
) {}
