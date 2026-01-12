package com.logistics.userauth.sms.application.port.in;

import com.logistics.userauth.sms.application.port.in.command.InternalSmsRateLimiterCommand;

/**
 * Входной порт (Inbound Port) проверки внутренних ограничений на отправку SMS.
 *
 * <h2>Назначение</h2>
 * Выделяет отдельный use case для контроля частоты отправки кодов верификации
 * (например, “не чаще 1 раза в N секунд” и/или лимиты по телефону/идентификатору).
 *
 * <h2>Когда используется</h2>
 * Обычно вызывается внутри сценария отправки кода перед генерацией/сохранением кода и отправкой SMS,
 * чтобы защититься от брутфорса и злоупотреблений.
 *
 * <h2>Входные данные</h2>
 * <ul>
 *   <li>{@link InternalSmsRateLimiterCommand} — данные, по которым проверяются лимиты (например, телефон).</li>
 * </ul>
 *
 * <h2>Ошибки</h2>
 * <ul>
 *   <li>{@link com.logistics.userauth.sms.application.exception.RateLimitExceededException} — лимит превышен.</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.application.usecase.InternalSmsRateLimiterService
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public interface InternalSmsRateLimiterUseCase {

    void checkRateLimiter(InternalSmsRateLimiterCommand command);
}
