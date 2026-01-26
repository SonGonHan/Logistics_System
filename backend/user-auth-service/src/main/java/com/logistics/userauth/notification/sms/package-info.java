/**
 * Модуль SMS верификации телефона микросервиса аутентификации.
 *
 * <h2>Назначение</h2>
 * Реализует процесс подтверждения номера телефона через отправку одноразового кода (OTP) по SMS:
 * генерация кода, хранение с TTL, проверка кода и контроль числа попыток/частоты отправки.
 *
 * <h2>Архитектура</h2>
 * Модуль построен по принципам Clean/Hexagonal Architecture:
 * <ul>
 *   <li><b>adapter.in</b> — REST endpoints и DTO</li>
 *   <li><b>application</b> — use case-логика и порты</li>
 *   <li><b>domain</b> — доменные модели (например, SmsVerificationCode)</li>
 *   <li><b>adapter.out</b> — интеграции (Redis/persistence, SMS провайдеры)</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.adapter.in
 * @see com.logistics.userauth.sms.application
 * @see com.logistics.userauth.sms.domain
 * @see com.logistics.userauth.sms.adapter.out
 */
package com.logistics.userauth.notification.sms;
