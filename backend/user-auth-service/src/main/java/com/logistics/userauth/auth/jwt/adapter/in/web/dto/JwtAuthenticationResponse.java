package com.logistics.userauth.auth.jwt.adapter.in.web.dto;

import lombok.Builder;

@Builder
public record JwtAuthenticationResponse (String accessToken, String refreshToken) {
}