package com.logistics.userauth.user.application.port.in.command;

import lombok.Builder;

@Builder
public record UpdateUserPersonalInfoCommand(
        Long userId,
        String firstName,
        String lastName,
        String middleName,
        String email
) {
}
