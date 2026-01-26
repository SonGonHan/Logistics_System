package com.logistics.userauth.notification.common.application.exception;

/**
 * Исключение при неверном или недействительном коде верификации.
 *
 * <h2>Когда выбрасывается</h2>
 * <ul>
 *   <li>Код для указанного идентификатора (телефон/email) не найден</li>
 *   <li>Срок действия кода истёк (TTL закончился)</li>
 *   <li>Введённый код не совпадает с сохранённым</li>
 *   <li>Превышено максимальное число попыток ввода кода</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 400 Bad Request
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public class InvalidVerificationCodeException extends RuntimeException {

    public InvalidVerificationCodeException(String message) {
        super(message);
    }

    public InvalidVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}