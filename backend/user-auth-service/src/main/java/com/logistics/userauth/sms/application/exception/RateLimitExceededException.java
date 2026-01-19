package com.logistics.userauth.sms.application.exception;

/**
 * Исключение при превышении лимита запросов на отправку SMS.
 *
 * <h2>Когда выбрасывается</h2>
 * <ul>
 *   <li>Повторная отправка кода раньше, чем через установленное время</li>
 *   <li>Превышение дневного лимита отправок с одного IP</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 429 Too Many Requests
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
