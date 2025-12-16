/**
 * Подсистема JWT-аутентификации.
 *
 * Использует:
 * <ul>
 *   <li>HS256 подписание с секретным ключом</li>
 *   <li>Access token - короткоживущий (5-15 минут)</li>
 *   <li>Refresh token - долгоживущий (7-30 дней, привязан к сессии)</li>
 *   <li>Token Rotation - отзыв старого refresh после обновления</li>
 * </ul>
 */
package com.logistics.userauth.auth.jwt;
