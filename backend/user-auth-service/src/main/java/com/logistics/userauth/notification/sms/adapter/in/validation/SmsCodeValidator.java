package com.logistics.userauth.notification.sms.adapter.in.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;



/**
 * Валидатор для аннотации {@link SmsCode}.
 *
 * <h2>Назначение</h2>
 * Проверяет, что SMS-код:
 * <ol>
 *   <li>Не равен {@code null} и не является пустой строкой.</li>
 *   <li>Состоит только из цифр.</li>
 *   <li>Имеет длину, заданную в {@code app.sms.verification.code-length}.</li>
 * </ol>
 *
 * <h2>Regex</h2>
 * Используется регулярное выражение вида {@code ^\\d{N}$}, где {@code N} — длина кода из конфигурации. [file:24]
 *
 * <h2>Пример использования</h2>
 * <pre>
 * public record VerifyPhoneRequest(
 *     &#64;Phone String phone,
 *     &#64;SmsCode String code
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
 * @see SmsCode
 */
@Component
public class SmsCodeValidator implements ConstraintValidator<SmsCode, String> {

    @Value("${app.sms.verification.code-length:6}")
    private int codeLength;

    private Pattern pattern;

    @Override
    public void initialize(SmsCode constraintAnnotation) {
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
