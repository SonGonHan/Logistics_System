package com.logistics.userauth.auth.session.adapter.in;

import com.logistics.userauth.audit.adapter.out.persistence.AuditLogEntity;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.auth.session.adapter.in.dto.UserSessionDTO;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapter.in.web.dto.UserDTO;
import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Domain UserSession и DTO.
 *
 * <h2>Назначение</h2>
 * Конвертирует UserSession → UserSessionDTO и обратно.
 *
 * @see UserSessionDTO для DTO
 * @see UserSession для доменной сущности
 */
@Component
public class UserSessionControllerMapper {

    public UserSession toDomain(UserSessionDTO userSessionDTO) {
        return UserSession.builder()
                .createdAt(userSessionDTO.createdAt())
                .ipAddress(userSessionDTO.ipAddress())
                .userAgent(userSessionDTO.userAgent())
                .expiresAt(userSessionDTO.expiresAt())
                .build();
    }

    public UserSessionDTO toDTO(UserSession userSession) {
        return UserSessionDTO.builder()
                .createdAt(userSession.getCreatedAt())
                .ipAddress(userSession.getIpAddress())
                .userAgent(userSession.getUserAgent())
                .expiresAt(userSession.getExpiresAt())
                .build();
    }
}
