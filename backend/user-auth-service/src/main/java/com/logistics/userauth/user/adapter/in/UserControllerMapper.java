package com.logistics.userauth.user.adapter.in;

import com.logistics.userauth.user.adapter.in.web.dto.UserDTO;
import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Маппер для преобразования между Domain User и DTO.
 *
 * <h2>Назначение</h2>
 * Конвертирует User → UserDTO и обратно.
 * НЕ передает пароль в DTO (по соображениям безопасности).
 *
 * @see UserDTO для DTO
 * @see User для доменной сущности
 */
@Component
public class UserControllerMapper {
    public static UserDTO toDTO(User domain) {
        return UserDTO.builder()
                .phone(domain.getPhone())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .middleName(domain.getMiddleName())
                .role(domain.getRole())
                .build();
    }

    public static User toDomain(UserDTO dto) {
        return User.builder()
                .phone(dto.phone())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .middleName(dto.middleName())
                .role(dto.role())
                .lastAccessedTime(LocalDateTime.now())
                .build();
    }
}
