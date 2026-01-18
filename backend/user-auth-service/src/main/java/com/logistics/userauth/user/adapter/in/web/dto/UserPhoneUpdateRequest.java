package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Phone;


public record UserPhoneUpdateRequest(
        @Phone
        String phone
) {
}
