package com.logistics.userauth.notification.email.application.port.out;

import com.logistics.userauth.notification.common.application.port.out.SendNotificationPort;

/**
 * Выходной порт для отправки email.
 * Реализуется провайдерами email (Mock, SMTP).
 */
public interface SendEmailPort extends SendNotificationPort {

}
