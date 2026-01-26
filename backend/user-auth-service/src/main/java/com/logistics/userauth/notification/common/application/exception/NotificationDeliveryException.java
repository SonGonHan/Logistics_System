package com.logistics.userauth.notification.common.application.exception;

/**
 * Базовое исключение при ошибке доставки уведомления (SMS, Email и т.д.).
 *
 * <h2>Причины</h2>
 * <ul>
 *   <li>Провайдер уведомлений недоступен</li>
 *   <li>Недостаточно средств на балансе</li>
 *   <li>Неверный формат получателя (телефон/email)</li>
 *   <li>Получатель заблокирован</li>
 *   <li>Технические проблемы с сетью или API</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 500 Internal Server Error или 503 Service Unavailable
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
public abstract class NotificationDeliveryException extends RuntimeException {

    protected NotificationDeliveryException(String message) {
        super(message);
    }

    protected NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
