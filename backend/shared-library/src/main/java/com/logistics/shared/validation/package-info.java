/**
 * Пакет с кастомными аннотациями и валидаторами для доменных моделей.
 *
 * Реализует интеграцию с Jakarta Bean Validation (ранее javax.validation).
 *
 * Доступные валидаторы:
 * <ul>
 *   <li><b>@Phone</b> - Номер телефона (РФ: +7..., Беларусь: +375..., Казахстан: +77...)</li>
 *   <li><b>@Password</b> - Сложный пароль (8+ символов, буквы, цифры, спецсимволы)</li>
 * </ul>
 *
 * Пример использования:
 * <pre>{@code
 * @Data
 * public class User {
 *     @Email
 *     private String email;
 *
 *     @Phone
 *     private String phone;
 *
 *     @Password
 *     private String password;
 * }
 *
 * // Валидация автоматическая при @Valid
 * @PostMapping("/register")
 * public void register(@Valid @RequestBody SignUpRequest request) { }
 * }</pre>
 *
 * @see jakarta.validation.Constraint
 * @see jakarta.validation.ConstraintValidator
 */
package com.logistics.shared.validation;