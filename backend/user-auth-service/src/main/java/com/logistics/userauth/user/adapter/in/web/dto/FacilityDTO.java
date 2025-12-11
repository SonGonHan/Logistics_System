package com.logistics.userauth.user.adapter.in.web.dto;

import lombok.Builder;

@Builder
public record FacilityDTO (String name, String address) {
}
