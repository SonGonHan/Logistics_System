package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Password;
import com.logistics.shared.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @Email
        String email,

        @NotNull
        @Phone
        String phone,

        @Password
        String password,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String middleName
) {}
