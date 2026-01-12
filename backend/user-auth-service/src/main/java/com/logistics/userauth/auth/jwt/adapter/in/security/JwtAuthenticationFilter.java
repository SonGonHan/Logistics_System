package com.logistics.userauth.auth.jwt.adapter.in.security;

import com.logistics.userauth.auth.jwt.adapter.out.JwtTokenProvider;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.auth.jwt.infrastructure.security.SecurityConfiguration;
import com.logistics.userauth.user.application.port.out.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Фильтр для аутентификации запросов на основе JWT токенов.
 *
 * Применяется ко ВСЕМ запросам кроме /auth/** endpoints.
 *
 * Процесс:
 * 1. Читает header Authorization
 * 2. Извлекает Bearer токен
 * 3. Валидирует токен через JwtTokenProvider
 * 4. Извлекает userId из токена
 * 5. Загружает пользователя из БД
 * 6. Создает Authentication объект и устанавливает в SecurityContext
 * 7. Передает запрос дальше по цепочке
 *
 * Если токен невалиден:
 * - Запрос передается дальше БЕЗ аутентификации
 * - Spring Security вернет 403 Forbidden для защищенных ресурсов
 *
 * Интеграция:
 * @see SecurityConfiguration где регистрируется этот фильтр
 * @see JwtTokenProvider для валидации токенов
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenGeneratorPort tokenGenerator;
    private final UserRepository userRepository;

    /**
     * Выполняет фильтрацию и аутентификацию.
     *
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param filterChain Цепочка фильтров
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HEADER_NAME);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!tokenGenerator.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        var userId = tokenGenerator.extractUserId(token);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                var authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
                var authToken = new UsernamePasswordAuthenticationToken(
                                user, null, authorities
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Исключает /auth/** endpoints из обработки этого фильтра.
     *
     * @param request HTTP запрос
     * @return true если запрос НЕ должен быть обработан этим фильтром
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth") ||
                path.startsWith("/api/v1/sms") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");
    }
}
