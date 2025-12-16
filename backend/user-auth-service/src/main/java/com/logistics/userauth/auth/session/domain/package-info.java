/**
 * Доменная модель пользовательской сессии.
 *
 * Представляет активную сессию пользователя, привязанную к refresh token.
 *
 * Поля:
 * <ul>
 *   <li><b>userId</b> - Какой пользователь</li>
 *   <li><b>refreshToken</b> - UUID refresh token</li>
 *   <li><b>ipAddress</b> - IP-адрес для привязки сессии</li>
 *   <li><b>userAgent</b> - Браузер/устройство для логирования</li>
 *   <li><b>expiresAt</b> - Когда истекает refresh token</li>
 *   <li><b>revoked</b> - Был ли отозван (logout)</li>
 * </ul>
 */
package com.logistics.userauth.auth.session.domain;