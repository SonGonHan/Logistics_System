package com.logistics.userauth.auth.jwt.adapter.out;

import com.logistics.userauth.auth.jwt.adapter.in.security.JwtAuthenticationFilter;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Провайдер для генерации и валидации JWT access токенов.
 *
 * Использует HS256 (HMAC SHA256) алгоритм подписания.
 *
 * Структура JWT:
 * - Header: { "alg": "HS256" }
 * - Payload: { "sub": "userId", "iat": timestamp, "exp": timestamp, "phone": "...", "role": "..." }
 * - Signature: HMAC(secret, header.payload)
 *
 * Конфигурация в application.yml:
 * app:
 *   jwt:
 *     secret: "your-secret-key-change-in-production"
 *     expiration: 3600  # 1 час в секундах
 *
 * @see JwtAuthenticationFilter для проверки токена в каждом запросе
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenGeneratorPort {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long accessTokenTtlSeconds;

    /**
     * Генерирует новый JWT access token для пользователя.
     *
     * @param user Пользователь, для которого создается токен
     * @return Подписанный JWT токен в виде строки
     */
    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenTtlSeconds);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim("phone", user.getPhone())
                .claim("role", user.getRole().name())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Проверяет валидность JWT токена.
     *
     * @param token JWT токен для проверки
     * @return true если токен валиден и не истек
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Извлекает ID пользователя из JWT токена.
     *
     * @param token JWT токен
     * @return ID пользователя (значение "sub" claim)
     */
    @Override
    public Long extractUserId(String token) {
        Claims claims = parseClaims(token);
        String sub = claims.getSubject();
        return Long.valueOf(sub);
    }

    /**
     * Создаёт HMAC-ключ подписи для JWT (HS256) из {@code app.jwt.secret}.
     *
     * @return SecretKey для подписи/проверки JWT.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Парсит JWT, проверяет подпись и возвращает {@link Claims}.
     *
     * @param token JWT (compact string).
     * @return Claims токена.
     * @throws JwtException Если токен невалиден (подпись/формат/exp и т.п.).
     * @throws IllegalArgumentException Если token некорректен.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
