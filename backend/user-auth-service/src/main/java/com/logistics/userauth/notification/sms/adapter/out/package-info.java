/**
 * Исходящие адаптеры (Outbound Adapters) модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Реализует выходные порты SMS модуля: отправку SMS через провайдер и хранение кодов/статусов в Redis.
 *
 * <h2>Подпакеты</h2>
 * <ul>
 *   <li><b>provider</b> — отправка SMS через внешние сервисы или mock-реализацию.</li>
 *   <li><b>persistence</b> — хранение кодов и статусов верификации (например, Redis).</li>
 * </ul>
 *
 * @see com.logistics.userauth.notification.sms.application.port.out.SendSmsPort
 * @see com.logistics.userauth.notification.sms.application.port.out.SmsRepository
 */
package com.logistics.userauth.notification.sms.adapter.out;
