/**
 * Подсистема управления пользовательскими сессиями.
 *
 * Отвечает за:
 * <ul>
 *   <li>Создание сессий (привязка refresh token к IP и User-Agent)</li>
 *   <li>Отзыв сессий (logout)</li>
 *   <li>Управление временем жизни сессии</li>
 * </ul>
 */
package com.logistics.userauth.auth.session;