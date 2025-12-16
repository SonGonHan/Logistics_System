package com.logistics.userauth.user.infrastructure;

import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Адаптер доменной модели User к интерфейсу UserDetails Spring Security.
 *
 * Реализует UserDetails, чтобы Spring Security мог использовать доменного
 * пользователя для аутентификации и авторизации.
 *
 * Основные функции:
 * - Возвращает Authorities на основе UserRole (преобразует CLIENT → ROLE_CLIENT)
 * - Предоставляет passwordHash как getPassword()
 * - Использует phone как getUsername() (основной идентификатор)
 * - Всегда возвращает true для isAccountNonExpired, isAccountNonLocked,
 *   isCredentialsNonExpired, isEnabled (логика блокировки в доменной модели)
 *
 * Пример использования:
 * ```java
 * User user = userRepository.findByPhone(phone);
 * UserDetails userDetails = new LogisticsUserDetails(user);
 * Authentication auth = new UsernamePasswordAuthenticationToken(
 *     userDetails, null, userDetails.getAuthorities()
 * );
 * SecurityContextHolder.getContext().setAuthentication(auth);
 * ```
 *
 * @see UserDetails
 * @see LogisticsUserDetailsService
 * @see User
 * @see UserRole
 */
@RequiredArgsConstructor
public class LogisticsUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getPhone();
    }

    public Long getId() {
        return user.getId();
    }


    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
