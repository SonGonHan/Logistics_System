/**
 * Входные порты (use cases) для операций JWT-аутентификации.
 *
 * Описывают сценарии использования системы:
 * <ul>
 *   <li><b>AuthenticateUserUseCase</b> - Вход пользователя (phone + password → tokens)</li>
 *   <li><b>RegisterUserUseCase</b> - Регистрация (email, phone, password → tokens)</li>
 *   <li><b>RefreshAccessTokenUseCase</b> - Обновление access token (старый refresh → новая пара)</li>
 *   <li><b>RevokeRefreshTokenUseCase</b> - Выход (отзыв refresh token)</li>
 *   <li><b>InternalCreateRefreshTokenUseCase</b> - Внутренний use case создания сессии</li>
 * </ul>
 *
 * Реализуются в пакете usecase.
 */
package com.logistics.userauth.auth.jwt.application.port.in;