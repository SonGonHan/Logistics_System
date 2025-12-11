package com.logistics.userauth.auth.session.adapter.in;

import com.logistics.userauth.auth.session.adapter.in.dto.UserSessionDTO;
import com.logistics.userauth.auth.session.domain.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionControllerMapper {

    public UserSession toUserSession(UserSessionDTO userSessionDTO) {
        return UserSession.builder()
                .createdAt(userSessionDTO.createdAt())
                .ipAddress(userSessionDTO.ipAddress())
                .userAgent(userSessionDTO.userAgent())
                .expiresAt(userSessionDTO.expiresAt())
                .build();
    }

    public UserSessionDTO toUserSessionDTO(UserSession userSession) {
        return UserSessionDTO.builder()
                .createdAt(userSession.getCreatedAt())
                .ipAddress(userSession.getIpAddress())
                .userAgent(userSession.getUserAgent())
                .expiresAt(userSession.getExpiresAt())
                .build();
    }
}
