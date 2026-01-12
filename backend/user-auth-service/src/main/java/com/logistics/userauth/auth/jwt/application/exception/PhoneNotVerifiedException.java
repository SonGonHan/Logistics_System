package com.logistics.userauth.auth.jwt.application.exception;

/**
 * Исключение при попытке регистрации без верификации телефона.
 *
 * <h2>Когда выбрасывается</h2>
 * <ul>
 *   <li>Пользователь пытается зарегистрироваться без подтверждения телефона</li>
 *   <li>Статус верификации истек (TTL 10 минут)</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 400 Bad Request
 *
 * <h2>Обработка</h2>
 * @ExceptionHandler в GlobalExceptionHandler возвращает:
 * {
 *   \"error\": \"PHONE_NOT_VERIFIED\",
 *   \"message\": \"...\"
 * }
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public class PhoneNotVerifiedException extends RuntimeException {
    public PhoneNotVerifiedException(String message) {
        super(message);
    }
}
