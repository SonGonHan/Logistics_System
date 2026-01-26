/**
 * Выходные порты (output ports) для модуля верификации.
 *
 * <p>Определяют интерфейсы для взаимодействия с внешними системами:</p>
 * <ul>
 *   <li>Отправка уведомлений (SMS, Email)</li>
 *   <li>Хранилище кодов верификации (Redis)</li>
 * </ul>
 *
 * @see com.logistics.userauth.notification.common.application.port.out.SendNotificationPort
 * @see com.logistics.userauth.notification.common.application.port.out.VerificationCodeRepository
 */
package com.logistics.userauth.notification.common.application.port.out;