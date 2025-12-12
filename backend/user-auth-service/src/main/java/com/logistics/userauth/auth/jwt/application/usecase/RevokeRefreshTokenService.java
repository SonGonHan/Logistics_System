package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.port.in.RevokeRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevokeRefreshTokenService implements RevokeRefreshTokenUseCase {

    private final UserSessionRepository repository;

    @Override
    public void revoke(RevokeRefreshTokenCommand command) {
        var session = repository.findByRefreshToken(command.refreshToken()).orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        session.setRevoked(true);
        repository.save(session);
    }
}
