/**
 * Инфраструктурные классы интеграции доменной модели пользователя со Spring Security.
 *
 * Реализует интерфейсы Spring Security:
 * <ul>
 *   <li><b>LogisticsUserDetails</b> - Адаптер User к UserDetails Spring Security</li>
 *   <li><b>LogisticsUserDetailsService</b> - Загрузка пользователя из БД по phone (username)</li>
 * </ul>
 *
 * Используется для:
 * <ul>
 *   <li>Аутентификации через JWT (JwtAuthenticationFilter)</li>
 *   <li>Проверки прав доступа к защищённым endpoint'ам</li>
 *   <li>Заполнения SecurityContext информацией о текущем пользователе</li>
 * </ul>
 */
package com.logistics.userauth.user.infrastructure;