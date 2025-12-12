package com.logistics.userauth.auth.session.adapter.out.persistence;

import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapter.out.persistence.UserPersistenceMapper;
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
                .refreshToken(entity.getRefreshToken())
                .expiresAt(entity.getExpiresAt())
                .ipAddress(entity.getIpAddress())
                .revoked(entity.isRevoked())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public UserSessionEntity toEntity(UserSession domain) {
        return UserSessionEntity.builder()
                .id(domain.getId())
                .user(upMapper.toEntity(domain.getUser()))
                .refreshToken(domain.getRefreshToken())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .revoked(domain.isRevoked())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .build();
    }
}
