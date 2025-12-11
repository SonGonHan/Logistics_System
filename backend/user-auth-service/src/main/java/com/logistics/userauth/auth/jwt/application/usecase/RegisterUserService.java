package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.RegisterUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;


    @Override
    public JwtAuthenticationResponse register(RegisterUserCommand command) {
        var user = User.builder()
                .email(command.email())
                .phone(command.phone())
                .passwordHash(passwordEncoder.encode(command.rawPassword()))
                .firstName(command.firstName())
                .lastName(command.lastName())
                .middleName(command.middleName())
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .lastAccessedTime(LocalDateTime.now())
                .build();

        var saved = userRepository.save(user);

        String token = tokenGenerator.generateAccessToken(saved);
        return new JwtAuthenticationResponse(token);
    }
}
