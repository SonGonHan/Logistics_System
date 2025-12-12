package com.logistics.userauth.auth.jwt.application.port.in.command;

import lombok.Builder;

@Builder
public record RegisterUserCommand(
        String email,
        String phone,
        String rawPassword,
        String firstName,
        String lastName,
        String middleName,
        String ipAddress,
        String userAgent
) { }
