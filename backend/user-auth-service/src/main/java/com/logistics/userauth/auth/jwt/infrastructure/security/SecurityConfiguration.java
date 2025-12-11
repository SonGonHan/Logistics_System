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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final LogisticsUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST + JWT => CSRF обычно отключаем
                .csrf(csrf -> csrf.disable())

                // Сессии нам не нужны, работаем stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Настройки авторизации по URL
                .authorizeHttpRequests(auth -> auth
                        // эндпоинты авторизации/регистрации — без токена
                        .requestMatchers("/auth/**").permitAll()
                        // при необходимости добавь swagger / actuator и т.п.
                        //.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // всё остальное — только с валидным JWT
                        .anyRequest().authenticated()
                )

                // Указываем, как аутентифицировать username/password (используется при логине)
                .authenticationProvider(authenticationProvider())

                // Регистрируем наш JWT-фильтр ПЕРЕД стандартным UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // сюда подложи свой UserDetailsService-адаптер поверх доменного UserRepository
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
