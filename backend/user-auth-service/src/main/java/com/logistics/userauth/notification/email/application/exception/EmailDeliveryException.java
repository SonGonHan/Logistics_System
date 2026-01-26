package com.logistics.userauth.notification.email.application.exception;

import com.logistics.userauth.notification.common.application.exception.NotificationDeliveryException;

/**
 * Исключение, выбрасываемое при ошибке отправки email.
 *
 * <h2>Причины</h2>
 * <ul>
 *   <li>SMTP-сервер недоступен</li>
 *   <li>Невалидный email адрес</li>
 *   <li>Проблемы с аутентификацией</li>
 *   <li>Email провайдер заблокирован</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 500 Internal Server Error или 503 Service Unavailable
 */
public class EmailDeliveryException extends NotificationDeliveryException {

    public EmailDeliveryException(String message) {
        super(message);
    }

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}