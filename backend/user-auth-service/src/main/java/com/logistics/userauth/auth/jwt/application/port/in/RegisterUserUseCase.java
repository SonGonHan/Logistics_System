package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;

public interface RegisterUserUseCase {
    JwtAuthenticationResponse register(RegisterUserCommand command);
}
