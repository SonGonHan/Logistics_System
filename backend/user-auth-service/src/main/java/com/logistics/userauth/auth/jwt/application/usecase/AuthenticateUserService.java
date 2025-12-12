package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.AuthenticateUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateUserService implements AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @Override
    public JwtAuthenticationResponse authenticate(AuthenticateUserCommand command) {
        var user = userRepository.findByPhone(command.phone())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var accessToken = tokenGenerator.generateAccessToken(user);

        var refreshToken = createRefreshTokenUseCase.create(
                CreateRefreshTokenCommand.builder()
                        .userId(user.getId())
                        .ipAddress(command.ipAddress())
                        .userAgent(command.userAgent())
                        .build()
        );
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }
}
