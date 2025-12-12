package com.logistics.userauth.auth.jwt.adapter.in.web;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.RefreshTokenRequest;
import com.logistics.userauth.auth.jwt.application.port.in.AuthenticateUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RefreshAccessTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RegisterUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RevokeRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RefreshAccessTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final RevokeRefreshTokenUseCase revokeRefreshTokenUseCase;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(
            @Valid @RequestBody SignInRequest request,
            HttpServletRequest httpRequest
    ) {
        var command = AuthenticateUserCommand.builder()
                .phone(request.phone())
                .password(request.password())
                .ipAddress(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();
        return ResponseEntity.ok(authenticateUserUseCase.authenticate(command));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        var command = RefreshAccessTokenCommand.builder()
                .refreshToken(request.refreshToken())
                .ipAddress(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();

        return ResponseEntity.ok(refreshAccessTokenUseCase.refresh(command));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        var command = RevokeRefreshTokenCommand.builder()
                .refreshToken(request.refreshToken())
                .build();

        revokeRefreshTokenUseCase.revoke(command);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JwtAuthenticationResponse> signUp(
            @Valid @RequestBody SignUpRequest request,
            HttpServletRequest httpRequest
    ) {
        var command = RegisterUserCommand.builder()
                .email(request.email())
                .phone(request.phone())
                .rawPassword(request.password())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .ipAddress(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();

        var response = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
