package com.logistics.userauth.notification.email.adapter.in.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Валидатор для аннотации {@link EmailCode}.
 *
 * <h2>Назначение</h2>
 * Проверяет, что Email-код:
 * <ol>
 *   <li>Не равен {@code null} и не является пустой строкой.</li>
 *   <li>Состоит только из цифр.</li>
 *   <li>Имеет длину, заданную в {@code app.email.verification.code-length}.</li>
 * </ol>
 *
 * <h2>Regex</h2>
 * Используется регулярное выражение вида {@code ^\\d{N}$}, где {@code N} — длина кода из конфигурации.
 *
 * <h2>Пример использования</h2>
 * <pre>
 * public record VerifyEmailRequest(
 *     &#64;Email String email,
 *     &#64;EmailCode String code
 * ) {}
 * </pre>
 *
 * <h2>Поведение</h2>
 * <ul>
 *   <li>Если значение {@code null} или пустое — возвращается {@code false}.</li>
 *   <li>Если значение содержит нецифровые символы — возвращается {@code false}.</li>
 *   <li>Если длина не совпадает с конфигурацией — возвращается {@code false}.</li>
 * </ul>
 *
 * @see EmailCode
 */
@Component
public class EmailCodeValidator implements ConstraintValidator<EmailCode, String> {

    @Value("${app.email.verification.code-length:6}")
    private int codeLength;

    private Pattern pattern;

    @Override
    public void initialize(EmailCode constraintAnnotation) {
        pattern = Pattern.compile("^\\d{" + codeLength + "}$");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return pattern.matcher(value).matches();
    }
}