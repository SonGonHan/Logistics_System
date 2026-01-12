/**
 * Исключения SMS модуля.
 *
 * <h2>Исключения</h2>
 * <ul>
 *   <li><b>{@link com.logistics.userauth.sms.application.exception.InvalidVerificationCodeException}</b> — 
 *       неверный код или истек срок действия</li>
 *   <li><b>{@link com.logistics.userauth.sms.application.exception.RateLimitExceededException}</b> — 
 *       превышен лимит запросов</li>
 *   <li><b>{@link com.logistics.userauth.sms.application.exception.SmsDeliveryException}</b> — 
 *       ошибка доставки SMS</li>
 * </ul>
 *
 * <h2>Обработка</h2>
 * Все исключения обрабатываются в {@link com.logistics.userauth.common.web.GlobalExceptionHandler}.
 *
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler
 */
package com.logistics.userauth.sms.application.exception;
