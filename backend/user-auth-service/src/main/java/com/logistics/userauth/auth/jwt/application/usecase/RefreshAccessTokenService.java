package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RefreshAccessTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RefreshAccessTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final UserSessionRepository repository;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @Override
    @Transactional
    public JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command) {
        var session = repository.findByRefreshToken(command.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        validate(session);

        String newAccessToken = tokenGenerator.generateAccessToken(session.getUser());

        session.setRevoked(true);
        repository.save(session);

        String newRefreshToken = createRefreshTokenUseCase.create(
                CreateRefreshTokenCommand.builder()
                        .userId(session.getUser().getId())
                        .ipAddress(command.ipAddress())
                        .userAgent(command.userAgent())
                        .build()
        );
        return new JwtAuthenticationResponse(newAccessToken, newRefreshToken);
    }

    private static void validate(UserSession session) {
        if (session.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token is revoked");
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token is expired");
        }
    }
}
