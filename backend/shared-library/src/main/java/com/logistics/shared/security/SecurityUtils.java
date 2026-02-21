package com.logistics.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Утилита для работы с JWT токенами в Spring Security контексте.
 *
 * <h2>Назначение</h2>
 * Предоставляет type-safe методы для извлечения данных из JWT токенов
 * без зависимости от конкретной реализации UserDetails. Используется
 * всеми микросервисами для единообразной работы с JWT claims.
 *
 * <h2>JWT Claims</h2>
 * Ожидаемая структура JWT токена:
 * <ul>
 *   <li>sub: userId (subject) - Long</li>
 *   <li>role: USER_ROLE (например, CLIENT, EMPLOYEE) - String</li>
 *   <li>phone: телефон пользователя - String</li>
 * </ul>
 *
 * <h2>Spring Security 7</h2>
 * Использует актуальные API Spring Security 7:
 * <ul>
 *   <li>{@link JwtAuthenticationToken} - стандартный Authentication для JWT</li>
 *   <li>{@link Jwt} - представление декодированного JWT токена</li>
 *   <li>Type-safe извлечение claims через методы {@link Jwt#getClaimAsString(String)}</li>
 * </ul>
 *
 * <h2>Использование</h2>
 * <pre>
 * {@code
 * @RestController
 * public class MyController {
 *     @GetMapping("/api/resource")
 *     public ResponseEntity<?> getResource(Authentication authentication) {
 *         Long userId = SecurityUtils.extractUserId(authentication);
 *         String role = SecurityUtils.extractRole(authentication);
 *         String phone = SecurityUtils.extractPhone(authentication);
 *         // ...
 *     }
 * }
 * }
 * </pre>
 *
 * @see JwtAuthenticationToken
 * @see Jwt
 */
public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Извлекает userId из JWT токена.
     *
     * <h3>Как это работает:</h3>
     * <ol>
     *   <li>Проверяет, что Authentication является {@link JwtAuthenticationToken}</li>
     *   <li>Получает {@link Jwt} токен из authentication</li>
     *   <li>Извлекает claim "sub" (subject) и конвертирует в Long</li>
     * </ol>
     *
     * @param authentication Spring Security Authentication (ожидается {@link JwtAuthenticationToken})
     * @return userId из JWT claim "sub"
     * @throws IllegalArgumentException если authentication null, не является JwtAuthenticationToken,
     *                                  или subject не может быть сконвертирован в Long
     */
    public static Long extractUserId(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        String subject = jwt.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("JWT subject (sub) claim is missing or empty");
        }

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid userId in JWT subject: expected Long, got '" + subject + "'", e
            );
        }
    }

    /**
     * Извлекает роль пользователя из JWT токена.
     *
     * <h3>Как это работает:</h3>
     * <ol>
     *   <li>Получает JWT токен из authentication</li>
     *   <li>Извлекает claim "role" как String</li>
     * </ol>
     *
     * @param authentication Spring Security Authentication (ожидается {@link JwtAuthenticationToken})
     * @return роль пользователя из JWT claim "role" (например, "CLIENT", "EMPLOYEE")
     * @throws IllegalArgumentException если authentication некорректный или claim "role" отсутствует
     */
    public static String extractRole(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        String role = jwt.getClaimAsString("role");

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("JWT 'role' claim is missing or empty");
        }

        return role;
    }

    /**
     * Извлекает номер телефона пользователя из JWT токена.
     *
     * <h3>Как это работает:</h3>
     * <ol>
     *   <li>Получает JWT токен из authentication</li>
     *   <li>Извлекает claim "phone" как String</li>
     * </ol>
     *
     * @param authentication Spring Security Authentication (ожидается {@link JwtAuthenticationToken})
     * @return номер телефона из JWT claim "phone"
     * @throws IllegalArgumentException если authentication некорректный или claim "phone" отсутствует
     */
    public static String extractPhone(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        String phone = jwt.getClaimAsString("phone");

        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("JWT 'phone' claim is missing or empty");
        }

        return phone;
    }

    /**
     * Внутренний helper метод для извлечения JWT из Authentication.
     *
     * <h3>Зачем нужен:</h3>
     * Централизует логику проверки типа Authentication и извлечения JWT токена.
     * Все публичные методы используют этот метод, что обеспечивает единообразную
     * обработку ошибок и упрощает поддержку кода.
     *
     * @param authentication Spring Security Authentication
     * @return декодированный JWT токен
     * @throws IllegalArgumentException если authentication null или не является JwtAuthenticationToken
     */
    private static Jwt extractJwt(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication is null");
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalArgumentException(
                    "Expected JwtAuthenticationToken, but got: " + authentication.getClass().getName() +
                    ". Ensure that the endpoint is protected by JWT authentication " +
                    "(Spring Security OAuth2 Resource Server configuration)."
            );
        }

        return jwtAuth.getToken();
    }
}
