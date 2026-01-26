/**
 * Web-адаптер (adapter.in) для модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Содержит REST контроллеры и DTO, которые принимают HTTP запросы и делегируют бизнес-логику
 * в application-слой через inbound ports.
 *
 * <h2>Состав</h2>
 * <ul>
 *   <li><b>SmsController</b> — endpoints отправки кода и проверки кода.</li>
 *   <li><b>DTO</b> — request/response модели для API.</li>
 * </ul>
 *
 * @see com.logistics.userauth.notification.sms.adapter.in.web.SmsController
 * @see com.logistics.userauth.notification.sms.application.port.in.SendPhoneVerificationCodeUseCase
 * @see com.logistics.userauth.notification.sms.application.port.in.VerifyPhoneUseCase
 */
package com.logistics.userauth.notification.sms.adapter.in.web;
