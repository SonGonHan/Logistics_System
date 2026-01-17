package com.logistics.userauth.auth.jwt.infrastructure.security;

import com.logistics.userauth.auth.jwt.adapter.in.security.JwtAuthenticationFilter;
import com.logistics.userauth.user.infrastructure.LogisticsUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Конфигурация Spring Security для JWT-based аутентификации.
 *
 * Особенности:
 * - Stateless сессии (не используются cookies и sessions)
 * - CSRF отключен (для REST API с JWT не требуется)
 * - JWT фильтр регистрируется в цепочке безопасности
 * - /auth/** endpoints открыты для всех
 * - Остальные endpoints требуют валидный JWT токен
 *
 * Архитектура:
 * 1. JwtAuthenticationFilter → извлекает и валидирует токен
 * 2. AuthenticationProvider → аутентифицирует user/password при логине
 * 3. SecurityFilterChain → определяет какие endpoint защищены
 *
 * @see JwtAuthenticationFilter для деталей обработки JWT
 * @see LogisticsUserDetailsService для загрузки пользователя из БД
 * @see CorsConfigurationSource для настройки межсетевых запросов
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final LogisticsUserDetailsService userDetailsService;

    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Определяет цепочку фильтров безопасности.
     *
     * @param http HttpSecurity для конфигурации
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/sms/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()

                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Провайдер аутентификации для username/password при логине.
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
