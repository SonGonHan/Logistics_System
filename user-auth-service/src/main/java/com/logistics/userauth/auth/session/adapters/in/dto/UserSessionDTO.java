package com.logistics.userauth.auth.session.adapters.in.dto;


import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSessionDTO {

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Inet ipAddress;

    private String userAgent;
}
