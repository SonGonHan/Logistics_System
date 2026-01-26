package com.logistics.userauth.notification.common.application.port.out;

/**
 * Выходной порт для отправки кодов верификации через различные каналы уведомлений.
 *
 * <p>Этот порт абстрагирует конкретную реализацию отправки (SMS, Email, Push и т.д.).</p>
 *
 * <h2>Реализации</h2>
 * <ul>
 *   <li>SMS - {@link com.logistics.userauth.notification.sms.adapter.out.provider.SmscSmsProvider}</li>
 *   <li>Email - {@link com.logistics.userauth.notification.email.adapter.out.provider.SmtpEmailProvider}</li>
 * </ul>
 */
public interface SendNotificationPort {

    /**
     * Отправляет код верификации на указанный идентификатор получателя.
     *
     * @param id идентификатор получателя
     * @param code код верификации для отправки
     * @return true если отправка успешна, false в противном случае
     */
    boolean sendVerificationCode(String id, String code);
}
