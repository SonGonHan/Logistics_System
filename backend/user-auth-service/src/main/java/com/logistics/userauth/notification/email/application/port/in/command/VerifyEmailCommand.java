package com.logistics.userauth.notification.email.application.port.in.command;

import lombok.Builder;

/**
 * Команда для верификации email с помощью кода.
 *
 * @param email адрес электронной почты
 * @param code код верификации
 */
@Builder
public record VerifyEmailCommand(String email, String code) {
}
