package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;

public interface InternalCreateRefreshTokenUseCase {
    String create(CreateRefreshTokenCommand command);
}
