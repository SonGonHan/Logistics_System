package com.logistics.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для проверки сложности паролей.
 *
 * <h2>Реализованные проверки</h2>
 * <ol>
 *   <li>Проверка на null</li>
 *   <li>Проверка длины (минимум 8 символов)</li>
 *   <li>Проверка на наличие заглавной буквы (?=.*[A-Z])</li>
 *   <li>Проверка на наличие строчной буквы (?=.*[a-z])</li>
 *   <li>Проверка на наличие цифры (?=.*\\\\d)</li>
 *   <li>Проверка на наличие спецсимвола (?=.*[^\\\\w\\\\s])</li>
 * </ol>
 *
 * <h2>Regex</h2>
 * ^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[^\\\\w\\\\s]).{8,}$
 *
 * Использует positive lookahead assertions для проверки всех условий.
 *
 * <h2>Примеры</h2>
 * <pre>
 * PasswordValidator validator = new PasswordValidator();
 *
 * validator.isValid(\"Password123!\", null);  // true
 * validator.isValid(\"password123!\", null);  // false (нет заглавной)
 * validator.isValid(\"Password123\", null);   // false (нет спецсимвола)
 * validator.isValid(\"Pass1!\", null);        // false (меньше 8 символов)
 * validator.isValid(null, null);             // false
 * </pre>
 *
 * @implements ConstraintValidator<Password, String>
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    // Минимум 8 символов, >=1 строчная, >=1 заглавная, >=1 цифра, >=1 спецсимвол
    private static final String REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches(REGEX);
    }
}
