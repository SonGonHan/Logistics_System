package com.logistics.userauth.user.application.port.in.command;

import lombok.Builder;

@Builder
public record UpdateUserPhoneCommand(
        Long userId,
        String phone
) {
}
