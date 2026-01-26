/**
 * Входные порты (Inbound Ports) модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * <p>
 * Пакет содержит контракты (интерфейсы) use case-ов, которые вызываются из adapter.in (REST контроллеров).
 * Это позволяет отделить web/API слой от бизнес-логики.
 * </p>
 *
 * <h2>Контракты</h2>
 * <ul>
 *   <li>{@link com.logistics.userauth.notification.sms.application.port.in.SendPhoneVerificationCodeUseCase}</li>
 *   <li>{@link com.logistics.userauth.notification.sms.application.port.in.VerifyPhoneUseCase}</li>
 *   <li>{@link com.logistics.userauth.notification.sms.application.port.in.InternalSmsRateLimiterUseCase}</li>
 * </ul>
 *
 * <h2>Входные данные</h2>
 * <p>
 * Команды для use case-ов вынесены в пакет {@code com.logistics.userauth.sms.application.port.in.command}.
 * </p>
 *
 * @author Logistics Team
 * @since 1.0.0
 */
package com.logistics.userauth.notification.sms.application.port.in;
