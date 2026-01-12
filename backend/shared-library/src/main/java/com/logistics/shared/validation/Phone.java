package com.logistics.shared.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Аннотация для валидации телефонных номеров.
 *
 * <h2>Поддерживаемые форматы</h2>
 * <ul>
 *   <li>Россия: +7XXXXXXXXXX, 8XXXXXXXXXX, +7 (XXX) XXX-XX-XX</li>
 *   <li>Беларусь: +375XXXXXXXXX, 376XXXXXXXXX</li>
 *   <li>Казахстан: +77XXXXXXXXX, 78XXXXXXXXX</li>
 * </ul>
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * \\@Phone
 * private String phone;
 *
 * \\@Phone(message = \"Неверный номер телефона\")
 * String phone;
 * </pre>
 *
 * <h2>Примеры валидных номеров</h2>
 * - 79991234567 (Россия)
 * - +7 (999) 123-45-67 (Россия с форматированием)
 * - +375291234567 (Беларусь)
 * - 77011234567 (Казахстан)
 *
 * <h2>Примеры невалидных номеров</h2>
 * - 123 (слишком короткий)
 * - +1 999 123 4567 (неподдерживаемая страна)
 * - 79991234 (неполный номер)
 *
 * @see PhoneValidator для реализации валидации
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "Неверный формат телефона";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
