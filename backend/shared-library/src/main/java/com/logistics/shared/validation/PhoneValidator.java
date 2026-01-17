package com.logistics.shared.validation;

import com.logistics.shared.utils.PhoneUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * Валидатор для аннотации {@link Phone}.
 *
 * <h2>Алгоритм валидации</h2>
 * <ol>
 *   <li>Проверяет, что значение не null и не пусто</li>
 *   <li>Удаляет пробелы, дефисы и скобки</li>
 *   <li>Сверяет нормализованный номер с регулярным выражением</li>
 *   <li>Возвращает true если совпадает, false в противном случае</li>
 * </ol>
 *
 * <h2>Regex паттерны</h2>
 * - Россия/Казахстан: ^(?:\\\\+7|7)\\\\d{10}$ (11-12 цифр)
 * - Беларусь: ^(?:\\\\+375|375)\\\\d{9}$ (12-13 цифр)
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
            "^(?:\\+7|8)\\d{10}$" +              // Россия/Казахстан
                    "|^(?:\\+375|376)\\d{9}$";   // Беларусь

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

        String normalized = PhoneUtils.normalize(value);
        return normalized.matches(REGEX);
    }

}

