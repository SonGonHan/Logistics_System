package com.logistics.userauth.auth.jwt.application.port.out;

import com.logistics.userauth.auth.jwt.adapter.out.JwtTokenProvider;
import com.logistics.userauth.user.domain.User;
import org.springframework.stereotype.Component;

/**
 * Порт для генерации и валидации JWT токенов.
 *
 * <h2>Методы</h2>
 * - generateAccessToken(user) - Создать новый access token
 * - isTokenValid(token) - Проверить валидность токена
 * - extractUserId(token) - Получить userId из токена
 *
 * <h2>Реализация</h2>
 * JwtTokenProvider
 *
 * @see JwtTokenProvider для реализации
 */
public interface TokenGeneratorPort {
    String generateAccessToken(User user);
    boolean isTokenValid(String token);
    Long extractUserId(String token);
}
