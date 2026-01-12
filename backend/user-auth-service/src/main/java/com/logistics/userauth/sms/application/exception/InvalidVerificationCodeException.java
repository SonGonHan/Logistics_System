package com.logistics.userauth.sms.application.exception;

/**
 * Исключение при неверном или недействительном коде SMS верификации.
 *
 * <h2>Когда выбрасывается</h2>
 * <ul>
 *   <li>Код для указанного телефона не найден (например, не запрашивали или уже удалён)</li>
 *   <li>Срок действия кода истёк (TTL закончился)</li>
 *   <li>Введённый код не совпадает с сохранённым</li>
 *   <li>Превышено максимальное число попыток ввода кода (код инвалидируется/удаляется)</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 400 Bad Request
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 * @see com.logistics.userauth.sms.application.usecase.VerifyPhoneService
 */
public class InvalidVerificationCodeException extends RuntimeException {

    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
