package com.logistics.userauth.auth.jwt.application.port.in.command;

public record AuthenticateUserCommand (String phone, String password) {
}
