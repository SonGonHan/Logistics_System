package com.logistics.userauth.user.application.port.in.command;

import lombok.Builder;

@Builder
public record UpdateUserPasswordCommand(
        Long userId,
        String oldPassword,
        String newPassword,
        String ipAddress,
        String userAgent
) {
}
