/**
 * Доменная модель модуля SMS верификации.
 *
 * <h2>Назначение</h2>
 * Описывает ключевые бизнес-объекты, используемые в сценариях подтверждения телефона,
 * без привязки к web/redis/базе данных.
 *
 * <h2>Классы</h2>
 * <ul>
 *   <li><b>SmsVerificationCode</b> — код верификации с метаданными (phone, code, expiresAt, attempts).</li>
 * </ul>
 *
 * @see com.logistics.userauth.sms.domain.SmsVerificationCode
 */
package com.logistics.userauth.sms.domain;
