package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

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
                .build();
    }
}
