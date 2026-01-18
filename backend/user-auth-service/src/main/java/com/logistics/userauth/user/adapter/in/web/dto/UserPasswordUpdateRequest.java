package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Password;

public record UserPasswordUpdateRequest(
        @Password
        String oldPassword,
        @Password
        String newPassword
) {
}
