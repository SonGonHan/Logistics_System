package com.logistics.userauth.notification.email.adapter.in.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Кастомная валидация для Email кода верификации.
 *
 * <h2>Правила</h2>
 * <ul>
 *   <li>Код должен содержать ровно N цифр (конфигурируемо, по умолчанию 6)</li>
 *   <li>Пробелы и тире не допускаются</li>
 *   <li>Только цифры 0-9</li>
 * </ul>
 *
 * <h2>Пример использования</h2>
 * <pre>
 * public record VerifyEmailRequest(
 *     @EmailCode String code
 * ) {}
 * </pre>
 *
 * @see EmailCodeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailCodeValidator.class)
public @interface EmailCode {
    String message() default "Код должен содержать {codeLength} цифр";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}