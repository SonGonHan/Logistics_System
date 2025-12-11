package com.logistics.shared.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PasswordValidator: юнит‑тесты")
class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    @DisplayName("Должен принимать достаточно сложные пароли")
    void shouldAcceptStrongPasswords() {
        assertThat(validator.isValid("Password123!", null)).isTrue();
        assertThat(validator.isValid("Qwerty1@", null)).isTrue();
    }

    @Test
    @DisplayName("Должен отклонять слабые пароли")
    void shouldRejectWeakPasswords() {
        assertThat(validator.isValid("short", null)).isFalse();          // короткий
        assertThat(validator.isValid("password", null)).isFalse();      // без цифр/символов
        assertThat(validator.isValid("password1", null)).isFalse();     // без спецсимвола
        assertThat(validator.isValid("PASSWORD1!", null)).isFalse();    // без строчных
        assertThat(validator.isValid("password1!", null)).isFalse();    // без заглавных
    }

    @Test
    @DisplayName("Должен отклонять null")
    void shouldRejectNull() {
        assertThat(validator.isValid(null, null)).isFalse();
    }
}
