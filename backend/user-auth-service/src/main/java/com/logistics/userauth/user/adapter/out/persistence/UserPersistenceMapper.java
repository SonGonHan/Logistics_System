package com.logistics.userauth.user.adapter.out.persistence;

import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Маппер для преобразования между Domain и JPA Entity.
 *
 * <h2>Назначение</h2>
 * Конвертирует User (доменная модель) ↔ UserEntity (JPA entity).
 * Позволяет скрыть детали БД от бизнес-слоя.
 *
 * @see User для доменной модели
 * @see UserEntity для JPA entity
 */
@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .passwordHash(domain.getPasswordHash())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .middleName(domain.getMiddleName())
                .role(domain.getRole())
                .facilityId(domain.getFacilityId())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedTime())
                .lastAccessedAt(domain.getLastAccessedTime())
                .build();
    }

    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .passwordHash(entity.getPasswordHash())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .role(entity.getRole())
                .facilityId(entity.getFacilityId())
                .status(entity.getStatus())
                .createdTime(entity.getCreatedAt())
                .lastAccessedTime(entity.getLastAccessedAt())
                .build();
    }
}
