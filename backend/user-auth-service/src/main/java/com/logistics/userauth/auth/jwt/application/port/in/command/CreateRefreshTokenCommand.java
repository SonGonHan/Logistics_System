package com.logistics.userauth.auth.jwt.application.port.in.command;

import lombok.Builder;

@Builder
public record CreateRefreshTokenCommand(
        Long userId,
        String ipAddress,
        String userAgent
){
}
