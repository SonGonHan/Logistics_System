package com.logistics.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * Валидатор для проверки корректности телефонных номеров.
 *
 * <h2>Алгоритм валидации</h2>
 * <ol>
 *   <li>Проверяет, что значение не null и не пусто</li>
 *   <li>Удаляет пробелы, дефисы и скобки</li>
 *   <li>Сверяет нормализованный номер с регулярным выражением</li>
 *   <li>Возвращает true если совпадает, false в противном случае</li>
 * </ol>
 *
 * <h2>Regex парттерны</h2>
 * - Россия: ^(?:\\\\+7|7)\\\\d{10}$ (11-12 цифр)
 * - Беларусь: ^(?:\\\\+375|375)\\\\d{9}$ (12-13 цифр)
 * - Казахстан: ^(?:\\\\+77|77)\\\\d{9}$ (11-12 цифр)
 *
 * <h2>Примеры</h2>
 * <pre>
 * PhoneValidator validator = new PhoneValidator();
 *
 * validator.isValid(\"79991234567\", null);      // true (Россия)
 * validator.isValid(\"+7 (999) 123-45-67\", null); // true (Россия форматированный)
 * validator.isValid(\"+375291234567\", null);    // true (Беларусь)
 * validator.isValid(\"123\", null);              // false (слишком короткий)
 * validator.isValid(null, null);                // false (null)
 * </pre>
 *
 * @implements ConstraintValidator<Phone, String>
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {


    private static final String REGEX =
            "^(?:\\+7|7)\\d{10}$" +              // Россия
                    "|^(?:\\+375|375)\\d{9}$" +         // Беларусь
                    "|^(?:\\+77|77)\\d{9}$";            // Казахстан

    /**
     * Валидирует телефонный номер.
     *
     * @param value Телефонный номер для валидации
     * @param context Контекст валидации
     * @return true если номер валиден, false в противном случае
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.replaceAll("[\\s\\-()]", "");
        return normalized.matches(REGEX);
    }
}

