package com.logistics.userauth.notification.sms.application.exception;

import com.logistics.userauth.notification.common.application.exception.NotificationDeliveryException;

/**
 * Исключение при ошибке доставки SMS.
 *
 * <h2>Причины</h2>
 * <ul>
 *   <li>SMS провайдер недоступен (SMSC down)</li>
 *   <li>Недостаточно средств на балансе</li>
 *   <li>Неверный формат номера телефона</li>
 *   <li>Номер заблокирован оператором</li>
 * </ul>
 *
 * <h2>HTTP статус</h2>
 * 500 Internal Server Error или 503 Service Unavailable
 */
public class SmsDeliveryException extends NotificationDeliveryException {

    public SmsDeliveryException(String message) {
        super(message);
    }

    public SmsDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
