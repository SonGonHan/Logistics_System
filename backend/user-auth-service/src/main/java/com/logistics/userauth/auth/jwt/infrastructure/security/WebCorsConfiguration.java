package com.logistics.userauth.auth.jwt.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Конфигурация CORS (Cross-Origin Resource Sharing) для REST API.
 *
 * <p>Разрешает межсайтовые HTTP-запросы от фронтенд-приложений на других доменах/портах.
 * Необходимо для работы React/Angular приложений на localhost:3000 с бэкендом на localhost:8080.</p>
 *
 * <h2>Настройки</h2>
 * <ul>
 *   <li><b>allowedOrigins</b> — localhost:3000, 127.0.0.1:3000 (для production указать реальные домены)</li>
 *   <li><b>allowedMethods</b> — GET, POST, PUT, PATCH, DELETE, OPTIONS</li>
 *   <li><b>allowedHeaders</b> — все заголовки (*)</li>
 *   <li><b>allowCredentials</b> — разрешена отправка cookies и Authorization header</li>
 *   <li><b>maxAge</b> — кэширование preflight-запросов на 1 час</li>
 * </ul>
 *
 * @see SecurityConfiguration
 * @see CorsConfigurationSource
 */
@Configuration
public class WebCorsConfiguration {

    /**
     * Создаёт источник CORS-конфигурации для всех endpoints.
     * Автоматически используется Spring Security для обработки preflight (OPTIONS) запросов.
     *
     * @return настроенный {@link CorsConfigurationSource} для применения ко всем URL
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:5601",
                "http://127.0.0.1:5601"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
