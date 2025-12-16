/**
 * Реализации use cases JWT-аутентификации.
 *
 * Содержат бизнес-логику:
 * <ul>
 *   <li><b>AuthenticateUserService</b> - Проверка пароля, генерация токенов</li>
 *   <li><b>RegisterUserService</b> - Хеширование пароля, создание пользователя</li>
 *   <li><b>RefreshAccessTokenService</b> - Token Rotation (отзыв старого, создание новых)</li>
 *   <li><b>RevokeRefreshTokenService</b> - Отзыв refresh token (logout)</li>
 *   <li><b>InternalCreateRefreshTokenService</b> - Создание новой сессии</li>
 * </ul>
 *
 * Каждый сервис реализует соответствующий port.in.
 */
package com.logistics.userauth.auth.jwt.application.usecase;