package com.logistics.userauth.auth.jwt.application.port.out;

import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

public interface TokenGeneratorPort {
    String generateAccessToken(User user);
    boolean isTokenValid(String token);
    Long extractUserId(String token);
}
