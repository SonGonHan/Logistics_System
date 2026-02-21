package com.logistics.shared.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PhoneValidator: юнит‑тесты")
class PhoneValidatorTest {

    private final PhoneValidator validator = new PhoneValidator();

    @Test
    @DisplayName("Должен принимать корректные номера РФ/РБ/КЗ")
    void shouldAcceptValidPhones() {
        assertThat(validator.isValid("89991234567", null)).isTrue(); // РФ
        assertThat(validator.isValid("+7 (999) 123-45-67", null)).isTrue(); // РФ с форматированием
        assertThat(validator.isValid("+375291234567", null)).isTrue(); // Беларусь
        assertThat(validator.isValid("376291234567", null)).isTrue();  // Беларусь без плюса
        assertThat(validator.isValid("+77011234567", null)).isTrue();  // Казахстан
        assertThat(validator.isValid("88011234567", null)).isTrue();   // Казахстан без плюса
    }

    @Test
    @DisplayName("Должен отклонять некорректные номера")
    void shouldRejectInvalidPhones() {
        assertThat(validator.isValid("123", null)).isFalse();
        assertThat(validator.isValid("abcdefghijk", null)).isFalse();
        assertThat(validator.isValid("+1 999 123 45 67", null)).isFalse(); // не наш регион
        assertThat(validator.isValid("+7999123456", null)).isFalse();      // мало цифр
        assertThat(validator.isValid("+799912345678", null)).isFalse();    // много цифр
    }

}
