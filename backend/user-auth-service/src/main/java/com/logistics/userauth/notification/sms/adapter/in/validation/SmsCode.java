package com.logistics.userauth.notification.sms.adapter.in.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * Аннотация валидации SMS-кода подтверждения.
 *
 * <h2>Назначение</h2>
 * Проверяет, что строка содержит только цифры и имеет длину, заданную в конфигурации приложения. 
 *
 * <h2>Конфигурация</h2>
 * Длина кода берётся из свойства {@code app.sms.verification.code-length}. 
 *
 * <h2>Требования к значению</h2>
 * <ul>
 *   <li>Значение не должно быть {@code null}.</li>
 *   <li>Значение не должно быть пустым или состоять только из пробелов.</li>
 *   <li>Значение должно содержать только цифры.</li>
 *   <li>Длина должна соответствовать {@code app.sms.verification.code-length}.</li>
 * </ul>
 *
 * <h2>Примеры</h2>
 * <pre>
 * &#64;SmsCode
 * private String code;
 * </pre>
 *
 * <h2>Примеры значений</h2>
 * Если {@code app.sms.verification.code-length = 6}: 
 * <ul>
 *   <li>Корректно: {@code "123456"}</li>
 *   <li>Некорректно: {@code "12345"}, {@code "1234567"}, {@code "12a456"}, {@code null}</li>
 * </ul>
 *
 * @see SmsCodeValidator
 */

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SmsCodeValidator.class)
public @interface SmsCode {
    String message() default "Код должен содержать {codeLength} цифр";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
