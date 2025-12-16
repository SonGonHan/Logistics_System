/**
 * Микросервис аутентификации и управления пользователями логистической системы.
 *
 * Основные функции:
 * <ul>
 *   <li><b>Аутентификация</b> - JWT-based (access + refresh tokens)</li>
 *   <li><b>Регистрация</b> - Создание новых пользователей</li>
 *   <li><b>Token Rotation</b> - Безопасное обновление токенов</li>
 *   <li><b>Session Management</b> - Управление сессиями пользователя</li>
 *   <li><b>Audit Logging</b> - Логирование всех действий в системе</li>
 * </ul>
 *
 * REST API endpoints:
 * <ul>
 *   <li>POST /auth/sign-up - Регистрация</li>
 *   <li>POST /auth/sign-in - Вход</li>
 *   <li>POST /auth/refresh - Обновление токена</li>
 *   <li>POST /auth/logout - Выход</li>
 * </ul>
 *
 * @see com.logistics.userauth.UserAuthServiceApplication
 */
package com.logistics.userauth;
