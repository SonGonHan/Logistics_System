/**
 * Выходные порты (Outbound Ports) модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Определяет контракты для взаимодействия use case-слоя с внешней инфраструктурой:
 * отправка SMS и хранение кодов/статусов верификации.
 *
 * <h2>Контракты</h2>
 * <ul>
 *   <li><b>SendSmsPort</b> — отправка кода в SMS провайдер.</li>
 *   <li><b>SmsRepository</b> — операции хранения/получения кода, статуса и попыток.</li>
 * </ul>
 *
 * <h2>Реализации</h2>
 * <ul>
 *   <li><b>Provider adapters</b>: {@code com.logistics.userauth.sms.adapter.out.provider}</li>
 *   <li><b>Persistence adapters</b>: {@code com.logistics.userauth.sms.adapter.out.persistence}</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.application.port.out.SendSmsPort
 * @see com.logistics.userauth.sms.application.port.out.SmsRepository
 */
package com.logistics.userauth.sms.application.port.out;
