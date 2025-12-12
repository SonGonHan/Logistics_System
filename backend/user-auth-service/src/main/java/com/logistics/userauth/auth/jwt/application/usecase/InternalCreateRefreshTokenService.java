package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.application.port.out.UserRepository;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalCreateRefreshTokenService implements InternalCreateRefreshTokenUseCase {

    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenTtlSeconds;

    @Override
    public String create(CreateRefreshTokenCommand command) {
        var user = userRepository.findById(command.userId()).orElseThrow(() ->  new RuntimeException("User not found"));

        String refreshToken = UUID.randomUUID().toString();

        var session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenTtlSeconds))
                .createdAt(LocalDateTime.now())
                .ipAddress(command.ipAddress() != null ? new Inet(command.ipAddress()): null)
                .userAgent(command.userAgent())
                .revoked(false)
                .build();

        sessionRepository.save(session);
        return refreshToken;
    }
}
