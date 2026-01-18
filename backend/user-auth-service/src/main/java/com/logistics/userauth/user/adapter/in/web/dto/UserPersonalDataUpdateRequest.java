package com.logistics.userauth.user.adapter.in.web.dto;

import jakarta.validation.constraints.Email;

public record UserPersonalDataUpdateRequest(
        String firstName,
        String lastName,
        String middleName,
        @Email
        String email
        ) {
}
