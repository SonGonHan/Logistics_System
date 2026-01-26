/**
 * Валидация входных данных SMS модуля (adapter.in).
 *
 * <h2>Назначение</h2>
 * Пакет содержит кастомные Jakarta Bean Validation-аннотации и валидаторы,
 * используемые на web/API слое для проверки корректности данных до вызова use case-ов.
 *
 * <h2>Состав</h2>
 * <ul>
 *   <li><b>{@link com.logistics.userauth.notification.sms.adapter.in.validation.SmsCode}</b> — аннотация для проверки формата SMS-кода.</li>
 *   <li><b>{@link com.logistics.userauth.notification.sms.adapter.in.validation.SmsCodeValidator}</b> — реализация проверки (цифры и длина из конфигурации).</li>
 * </ul>
 *
 * <h2>Конфигурация</h2>
 * Длина кода берётся из свойства {@code app.sms.verification.code-length}.
 *
 * <h2>Пример использования</h2>
 * <pre>{@code
 * public record VerifyPhoneRequest(
 *     @Phone String phone,
 *     @SmsCode String code
 * ) {}
 * }</pre>
 *
 * @see com.logistics.userauth.notification.sms.adapter.in.web.dto.VerifyPhoneRequest
 * @see jakarta.validation.Constraint
 * @see jakarta.validation.ConstraintValidator
 */
package com.logistics.userauth.notification.sms.adapter.in.validation;