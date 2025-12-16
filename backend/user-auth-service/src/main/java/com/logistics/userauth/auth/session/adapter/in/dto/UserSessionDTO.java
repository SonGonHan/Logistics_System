package com.logistics.userauth.auth.session.adapter.in.dto;


import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи информации о сессиях в ответах.
 *
 * <h2>Назначение</h2>
 * Содержит информацию сессии для отправки клиенту.
 *
 * <h2>Примеры</h2>
 * {
 *   \"createdAt\": \"15.12.2025\",
 *   \"expiresAt\": \"20.12.2025\",
 *   \"ipAddress\": \"192.168.0.10\",
 *   \"userAgent\": \"Mozilla\"
 * }
 */
@Builder
public record UserSessionDTO (LocalDateTime createdAt,
                              LocalDateTime expiresAt,
                              Inet ipAddress,
                              String userAgent) {
}
