package com.logistics.userauth.sms.application.port.in.command;

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
 * @see com.logistics.userauth.sms.application.port.in.InternalSmsRateLimiterUseCase
 */
public record InternalSmsRateLimiterCommand(String phone) {
}
