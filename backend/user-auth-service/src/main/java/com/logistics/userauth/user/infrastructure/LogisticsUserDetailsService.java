package com.logistics.userauth.user.infrastructure;

import com.logistics.userauth.auth.jwt.adapter.in.security.JwtAuthenticationFilter;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация UserDetailsService для загрузки пользователей из базы данных.
 *
 * Интегрирует доменный репозиторий UserRepository с механизмом
 * аутентификации Spring Security.
 *
 * Процесс загрузки пользователя:
 * 1. Принимает phone как username (Spring Security вызывает с username)
 * 2. Ищет пользователя в БД через UserRepository.findByPhone()
 * 3. Если не найден - выбрасывает UsernameNotFoundException
 * 4. Если найден - оборачивает в LogisticsUserDetails
 *
 * Используется:
 * - При аутентификации через JWT (JwtAuthenticationFilter)
 * - При аутентификации через форму логина (если включена)
 *
 * Пример workflow:
 * ```java
 * // Spring Security вызывает автоматически:
 * UserDetails userDetails = userDetailsService.loadUserByUsername("+79991234567");
 * // userDetails содержит все данные для проверки прав доступа
 * ```
 *
 * @see UserDetailsService
 * @see LogisticsUserDetails
 * @see UserRepository#findByPhone(String)
 * @see JwtAuthenticationFilter
 */
@Service
@RequiredArgsConstructor
public class LogisticsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает пользователя по номеру телефона.
     *
     * @param phone номер телефона пользователя (используется как username)
     * @return UserDetails с полной информацией о пользователе
     * @throws UsernameNotFoundException если пользователь с таким телефоном не найден
     */
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phone));
        return new LogisticsUserDetails(user);
    }
}
