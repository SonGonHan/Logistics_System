/**
 * Domain entity для управления пользовательскими сессиями.
 *
 * <h2>Назначение</h2>
 * Представляет сессию пользователя: refresh token, metadata запроса (IP, User-Agent),
 * TTL (время истечения), и статус (revoked=true при logout).
 *
 * <h2>Структура UserSession</h2>
 * <ul>
 *   <li><b>id</b> - Primary key (auto-generated BIGSERIAL)</li>
 *   <li><b>user</b> - User, владелец session'а</li>
 *   <li><b>refreshToken</b> - UUID строка (уникальный для каждой session'а)</li>
 *   <li><b>createdAt</b> - Timestamp создания (устанавливается @CreatedDate)</li>
 *   <li><b>expiresAt</b> - Timestamp истечения (обычно +30 дней от создания)</li>
 *   <li><b>ipAddress</b> - IP-адрес из request'а (тип Inet, PostgreSQL native)</li>
 *   <li><b>userAgent</b> - User-Agent header из request'а (браузер, версия, ОС)</li>
 *   <li><b>revoked</b> - Flag логаута (true если logout вызван)</li>
 * </ul>
 *
 * <h2>Жизненный цикл</h2>
 * <ol>
 *   <li>Создание: {@link com.logistics.userauth.auth.jwt.application.usecase.RegisterUserService} создает session</li>
 *   <li>Использование: {@link com.logistics.userauth.auth.jwt.application.usecase.RefreshAccessTokenService} проверяет session</li>
 *   <li>Отзыв: {@link com.logistics.userauth.auth.jwt.application.usecase.RevokeRefreshTokenService} устанавливает revoked=true</li>
 *   <li>Удаление: можно удалить по expiresAt < NOW (background job)</li>
 * </ol>
 *
 * <h2>Token Rotation</h2>
 * При refresh access token'а:
 * <ol>
 *   <li>Клиент отправляет old refresh token</li>
 *   <li>Сервер проверяет: expiresAt > NOW, revoked=false</li>
 *   <li>Генерирует новые access и refresh token'ы</li>
 *   <li>Создает новую session запись</li>
 *   <li>Стара session может остаться в БД (для audit log'ов)</li>
 * </ol>
 *
 * @see com.logistics.userauth.auth.session.adapter.out.persistence.UserSessionEntity
 */
package com.logistics.userauth.auth.session.domain;