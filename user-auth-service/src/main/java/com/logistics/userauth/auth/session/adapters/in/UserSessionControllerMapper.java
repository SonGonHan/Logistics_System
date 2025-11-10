package com.logistics.userauth.auth.session.adapters.in;

import com.logistics.userauth.auth.session.adapters.in.dto.UserSessionDTO;
import com.logistics.userauth.auth.session.domain.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionControllerMapper {

    public UserSession toUserSession(UserSessionDTO userSessionDTO) {
        return UserSession.builder()
                .createdAt(userSessionDTO.getCreatedAt())
                .ipAddress(userSessionDTO.getIpAddress())
                .userAgent(userSessionDTO.getUserAgent())
                .expiresAt(userSessionDTO.getExpiresAt())
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
