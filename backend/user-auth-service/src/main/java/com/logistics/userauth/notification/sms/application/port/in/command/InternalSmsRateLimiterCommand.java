package com.logistics.userauth.notification.sms.application.port.in.command;

import com.logistics.userauth.notification.sms.application.port.in.InternalSmsRateLimiterUseCase;

/**
 * Команда для внутренней проверки rate limit на отправку SMS кода.
 *
 * <h2>Назначение</h2>
 * Содержит данные, по которым применяется ограничение частоты отправки кода,
 * чтобы защититься от спама и злоупотреблений (например, ограничение по телефону).
 *
 * <h2>Поля</h2>
 * <ul>
 *   <li><b>phone</b> — номер телефона, для которого проверяется лимит отправки.</li>
 * </ul>
 *
 * <h2>Пример</h2>
 * <pre>{@code
 * var command = new InternalSmsRateLimiterCommand("79991234567");
 * internalSmsRateLimiterUseCase.checkRateLimiter(command);
 * }</pre>
 *
 * @see InternalSmsRateLimiterUseCase
 */
public record InternalSmsRateLimiterCommand(String phone) {
}
