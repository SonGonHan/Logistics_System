package com.logistics.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Аннотация для валидации сложности пароля.
 *
 * <h2>Требования к паролю</h2>
 * <ul>
 *   <li>Минимум 8 символов</li>
 *   <li>Минимум одна заглавная буква (A-Z)</li>
 *   <li>Минимум одна строчная буква (a-z)</li>
 *   <li>Минимум одна цифра (0-9)</li>
 *   <li>Минимум один спецсимвол (!@#$%^&*)</li>
 * </ul>
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * //@Password
 * private String password;
 *
 * //@Password(message = \"Пароль слишком слабый\")
 * String password;
 * </pre>
 *
 * <h2>Примеры валидных паролей</h2>
 * <ul>
 *   <li>Password123!</li>
 *   <li>Qwerty1@</li>
 *   <li>MyP@ssw0rd</li>
 *   <li>Admin#2025!</li>
 * </ul>
 *
 * <h2>Примеры невалидных паролей</h2>
 * <ul>
 *   <li>password123 (нет заглавной и спецсимвола)</li>
 *   <li>Password! (нет цифр)</li>
 *   <li>Pass1! (меньше 8 символов)</li>
 *   <li>PASSWORD123! (нет строчных)</li>
 * </ul>
 *
 * @see PasswordValidator для реализации валидации
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default
            "Пароль должен содержать минимум 8 символов, " +
                    "включая цифру, заглавную и строчную букву и спецсимвол";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

