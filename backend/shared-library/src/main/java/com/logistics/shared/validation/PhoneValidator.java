package com.logistics.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {


    private static final String REGEX =
            "^(?:\\+7|7)\\d{10}$" +              // Россия
                    "|^(?:\\+375|375)\\d{9}$" +         // Беларусь
                    "|^(?:\\+77|77)\\d{9}$";            // Казахстан

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.replaceAll("[\\s\\-()]", "");
        return normalized.matches(REGEX);
    }
}

