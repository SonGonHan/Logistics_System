package com.logistics.userauth.auth.session.domain;

import com.logistics.userauth.auth.session.adapter.out.persistence.UserSessionEntity;
import com.logistics.userauth.user.domain.User;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Доменная сущность для сессии пользователя.
 *
 * <h2>Назначение</h2>
 * Представляет активную сессию пользователя с refresh токеном.
 * Привязана к конкретному устройству (IP + User-Agent).
 *
 * <h2>Структура</h2>
 * - id: Уникальный идентификатор сессии
 * - user: Пользователь этой сессии
 * - refreshToken: UUID токен для обновления access token
 * - createdAt: Когда была создана сессия
 * - expiresAt: Когда истекает refresh token
 * - revoked: Был ли токен отозван (logout)
 * - ipAddress: IP-адрес клиента для защиты
 * - userAgent: User-Agent браузера для защиты
 *
 * <h2>Примеры</h2>
 * <pre>
 * UserSession session = UserSession.builder()
 *   .user(user)
 *   .refreshToken(UUID.randomUUID().toString())
 *   .expiresAt(LocalDateTime.now().plusDays(30))
 *   .ipAddress(new Inet(\"192.168.1.1\"))
 *   .userAgent(\"Mozilla/5.0...\")
 *   .revoked(false)
 *   .build();
 * </pre>
 *
 * @see User для пользователя
 * @see UserSessionEntity для JPA entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {

    private long id;

    private User user;

    private String refreshToken;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Inet ipAddress;

    private String userAgent;

    private boolean revoked;

}
