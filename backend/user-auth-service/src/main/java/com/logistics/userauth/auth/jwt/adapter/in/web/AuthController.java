package com.logistics.userauth.auth.jwt.adapter.in.web;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.AuthenticateUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RegisterUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;
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

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(
            @Valid @RequestBody SignInRequest request
    ) {
        var command = new AuthenticateUserCommand(
                request.phone(),
                request.password()
        );
        return ResponseEntity.ok(authenticateUserUseCase.authenticate(command));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JwtAuthenticationResponse> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        var command = new RegisterUserCommand(
                request.email(),
                request.phone(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.middleName()
        );

        var response = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
