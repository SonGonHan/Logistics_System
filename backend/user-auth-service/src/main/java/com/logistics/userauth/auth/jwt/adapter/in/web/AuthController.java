package com.logistics.userauth.auth.jwt.adapter.in.web;

import com.logistics.userauth.auth.jwt.adapter.in.security.JwtAuthenticationFilter;
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
import com.logistics.userauth.common.api.LogoutOperation;
import com.logistics.userauth.common.api.RefreshOperation;
import com.logistics.userauth.common.api.SignInOperation;
import com.logistics.userauth.common.api.SignUpOperation;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для аутентификации и работы с токенами.
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li>POST /api/v1/auth/register — Регистрация нового пользователя</li>
 *   <li>POST /api/v1/auth/sign-in — Вход в систему</li>
 *   <li>POST /api/v1/auth/refresh — Обновление access token</li>
 *   <li>POST /api/v1/auth/logout — Отзыв refresh token</li>
 * </ul>
 *
 * <h2>Security</h2>
 * Все endpoints исключены из JWT фильтра (shouldNotFilter).
 * Каждый endpoint имеет собственную валидацию.
 *
 * @see JwtAuthenticationFilter где исключаются эти endpoints
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(
        name = "Аутентификация",
        description = "REST API endpoints для регистрации, логина, refresh токенов и выхода из системы"
)
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final RevokeRefreshTokenUseCase revokeRefreshTokenUseCase;

    /**
     * POST /auth/sign-in
     * Вход в систему (аутентификация по телефону + пароль).
     */
    @PostMapping("/sign-in")
    @SignInOperation
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

    /**
     * POST /auth/register
     * Регистрация нового пользователя.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @SignUpOperation
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

    /**
     * POST /auth/logout
     * Выход из системы (отзыв refresh токена).
     */
    @PostMapping("/logout")
    @LogoutOperation
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        var command = RevokeRefreshTokenCommand.builder()
                .refreshToken(request.refreshToken())
                .build();

        revokeRefreshTokenUseCase.revoke(command);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /auth/refresh
     * Обновление access токена (token rotation).
     */
    @PostMapping("/refresh")
    @RefreshOperation
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

}
