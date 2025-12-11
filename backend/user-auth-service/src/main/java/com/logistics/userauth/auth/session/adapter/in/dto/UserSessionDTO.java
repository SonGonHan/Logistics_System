package com.logistics.userauth.auth.session.adapter.in.dto;


import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record UserSessionDTO (LocalDateTime createdAt,
                              LocalDateTime expiresAt,
                              Inet ipAddress,
                              String userAgent) {
}
