package com.logistics.userauth.user.application.port.in.command;

/**
 * Команда для поиска или создания пользователя по номеру телефона.
 *
 * <p>Используется при создании накладной: если получателя нет в системе,
 * он регистрируется как CLIENT с минимальными данными.
 */
public record EnsureUserByPhoneCommand(String phone) {
}