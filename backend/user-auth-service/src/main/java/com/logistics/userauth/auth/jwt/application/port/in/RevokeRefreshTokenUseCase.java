package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;

public interface RevokeRefreshTokenUseCase {
    void revoke(RevokeRefreshTokenCommand command);
}
