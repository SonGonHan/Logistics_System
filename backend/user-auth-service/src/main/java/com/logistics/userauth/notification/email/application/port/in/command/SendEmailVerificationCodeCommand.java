package com.logistics.userauth.notification.email.application.port.in.command;

/**
 * Команда для отправки кода верификации на email.
 *
 * @param email адрес электронной почты
 */
public record SendEmailVerificationCodeCommand(String email) {
}
