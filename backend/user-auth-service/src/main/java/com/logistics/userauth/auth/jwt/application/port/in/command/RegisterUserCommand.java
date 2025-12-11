package com.logistics.userauth.auth.jwt.application.port.in.command;

public record RegisterUserCommand(
        String email,
        String phone,
        String rawPassword,
        String firstName,
        String lastName,
        String middleName
) { }
