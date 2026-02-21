package com.logistics.userauth.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record EnsureUserByPhoneRequest(
        @NotBlank(message = "Номер телефона обязателен")
        String phone
) {
}