package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.userauth.user.domain.UserRole;
import lombok.Builder;

/**
 * DTO для передачи информации о пользователе в ответах.
 *
 * <h2>Назначение</h2>
 * Содержит публичную информацию пользователя для отправки клиенту.
 * НЕ содержит чувствительной информации (пароль, passwordHash).
 *
 * <h2>Примеры</h2>
 * {
 *   \"phone\": \"+79991234567\",
 *   \"firstName\": \"Иван\",
 *   \"lastName\": \"Иванов\",
 *   \"role\": \"CLIENT\"
 * }
 */
@Builder
public record UserDTO (String phone,
                       String firstName,
                       String lastName,
                       String middleName,
                       UserRole role){

}
