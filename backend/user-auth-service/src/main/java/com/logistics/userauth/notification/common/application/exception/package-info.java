/**
 * Общие исключения для модуля верификации через уведомления.
 *
 * <p>Этот пакет содержит базовые исключения, используемые всеми каналами уведомлений
 * (SMS, Email, Push и т.д.).</p>
 *
 * <h2>Иерархия исключений</h2>
 * <ul>
 *   <li>{@link com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException} - неверный код</li>
 *   <li>{@link com.logistics.userauth.notification.common.application.exception.RateLimitExceededException} - превышен лимит запросов</li>
 *   <li>{@link com.logistics.userauth.notification.common.application.exception.NotificationDeliveryException} - базовое для ошибок доставки</li>
 * </ul>
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
package com.logistics.userauth.notification.common.application.exception;