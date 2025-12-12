package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.RefreshAccessTokenCommand;

public interface RefreshAccessTokenUseCase {
    JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command);
}
