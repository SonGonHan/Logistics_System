package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;

public interface AuthenticateUserUseCase {
    JwtAuthenticationResponse authenticate(AuthenticateUserCommand command);
}
