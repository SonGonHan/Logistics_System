package com.logistics.userauth.notification.email.application.port.in.command;

/**
 * Команда для внутренней проверки rate limit.
 *
 * @param email адрес электронной почты для проверки
 */
public record InternalEmailRateLimiterCommand(String email) {
}
