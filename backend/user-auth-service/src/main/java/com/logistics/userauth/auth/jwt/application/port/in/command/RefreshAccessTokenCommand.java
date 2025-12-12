package com.logistics.userauth.auth.jwt.application.port.in.command;

import lombok.Builder;

@Builder
public record RefreshAccessTokenCommand (
        String refreshToken,
        String ipAddress,
        String userAgent) {
}
