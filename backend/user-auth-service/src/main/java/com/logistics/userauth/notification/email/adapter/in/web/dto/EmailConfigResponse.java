package com.logistics.userauth.notification.email.adapter.in.web.dto;

import lombok.Builder;

/**
 * DTO конфигурации email-верификации.
 *
 * <h2>Назначение</h2>
 * Возвращает клиенту параметры для корректной работы UI
 * (например, таймер для повторной отправки кода).
 *
 * @param resendCooldownSeconds количество секунд перед возможностью повторной отправки
 */
@Builder
public record EmailConfigResponse(
        Long resendCooldownSeconds
) {
}