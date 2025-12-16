/**
 * Пакет CQRS-команд для use cases JWT-аутентификации.
 *
 * Каждая команда - неизменяемый объект-значение, инкапсулирующий входные данные
 * конкретного use case:
 * <ul>
 *   <li><b>AuthenticateUserCommand</b> - phone, password, ipAddress, userAgent</li>
 *   <li><b>RegisterUserCommand</b> - email, phone, password, ФИО, ipAddress, userAgent</li>
 *   <li><b>RefreshAccessTokenCommand</b> - refreshToken, ipAddress, userAgent</li>
 *   <li><b>RevokeRefreshTokenCommand</b> - refreshToken</li>
 *   <li><b>CreateRefreshTokenCommand</b> - userId, ipAddress, userAgent</li>
 * </ul>
 *
 * Используются для:
 * <ul>
 *   <li>Явной передачи всех параметров (уменьшает ошибки)</li>
 *   <li>Маппинга из веб-DTO в доменные объекты</li>
 *   <li>Тестирования (легко создавать test doubles)</li>
 * </ul>
 */
package com.logistics.userauth.auth.jwt.application.port.in.command;