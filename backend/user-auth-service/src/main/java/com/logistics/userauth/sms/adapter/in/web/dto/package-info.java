/**
 * DTO слоя REST API для SMS верификации.
 *
 * <h2>Назначение</h2>
 * Содержит структуры данных запросов/ответов, используемые REST контроллерами SMS модуля.
 * DTO покрывают операции отправки кода и подтверждения телефона.
 *
 * <h2>DTO</h2>
 * <ul>
 *   <li><b>SendVerificationCodeRequest</b> — запрос на отправку кода (phone).</li>
 *   <li><b>VerifyPhoneRequest</b> — запрос на верификацию (phone + code).</li>
 * </ul>
 *
 * <h2>Валидация</h2>
 * <ul>
 *   <li>{@link com.logistics.shared.validation.Phone} — формат телефона.</li>
 *   <li>{@link com.logistics.userauth.sms.adapter.in.validation.SmsCode} — формат SMS кода.</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.adapter.in.web.dto.SendVerificationCodeRequest
 * @see com.logistics.userauth.sms.adapter.in.web.dto.VerifyPhoneRequest
 */
package com.logistics.userauth.sms.adapter.in.web.dto;
