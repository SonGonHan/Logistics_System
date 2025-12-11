package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Password;
import com.logistics.shared.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignInRequest (
        @NotNull
        @Phone
        String phone,

        @Email
        String email,

        @Password
        String password
) { }