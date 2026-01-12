/**
 * Use case-слой SMS верификации телефона.
 *
 * <h2>Назначение</h2>
 * Реализует бизнес-сценарии отправки кода и подтверждения телефона, включая ограничения
 * на повторную отправку и число попыток ввода кода.
 *
 * <h2>Ключевые сервисы</h2>
 * <ul>
 *   <li><b>SendVerificationCodeService</b> — генерация кода, сохранение с TTL и отправка через провайдера.</li>
 *   <li><b>VerifyPhoneService</b> — проверка кода, учет попыток, установка статуса “verified”.</li>
 *   <li><b>InternalSmsRateLimiterService</b> — rate limit на отправку SMS.</li>
 * </ul>
 *
 * <h2>Ошибки</h2>
 * <ul>
 *   <li>{@link com.logistics.userauth.sms.application.exception.RateLimitExceededException} — превышен лимит.</li>
 *   <li>{@link com.logistics.userauth.sms.application.exception.SmsDeliveryException} — ошибка отправки SMS.</li>
 *   <li>{@link com.logistics.userauth.sms.application.exception.InvalidVerificationCodeException} — неверный/истекший код.</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.application.usecase.SendVerificationCodeService
 * @see com.logistics.userauth.sms.application.usecase.VerifyPhoneService
 * @see com.logistics.userauth.sms.application.usecase.InternalSmsRateLimiterService
 */
package com.logistics.userauth.sms.application.usecase;
