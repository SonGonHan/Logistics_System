package com.logistics.userauth.auth.session.domain;

import com.logistics.userauth.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {

    private long id;

    private User user;

    private String sessionToken;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String ipAddress;

    private String userAgent;

}
