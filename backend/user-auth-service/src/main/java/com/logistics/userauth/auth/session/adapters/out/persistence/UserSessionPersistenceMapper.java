package com.logistics.userauth.auth.session.adapters.out.persistence;

import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapters.out.persistence.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionPersistenceMapper {

    private final UserPersistenceMapper upMapper;

    public UserSession toDomain(UserSessionEntity entity) {
        return UserSession.builder()
                .id(entity.getId())
                .user(upMapper.toDomain(entity.getUser()))
                .expiresAt(entity.getExpiresAt())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .sessionToken(entity.getSessionToken())
                .build();
    }

    public UserSessionEntity toEntity(UserSession domain) {
        return UserSessionEntity.builder()
                .id(domain.getId())
                .user(upMapper.toEntity(domain.getUser()))
                .sessionToken(domain.getSessionToken())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .build();
    }
}
