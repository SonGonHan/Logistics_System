package com.logistics.userauth.auth.session.adapter.out.persistence;

import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLInetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
/**
 * JPA Entity для хранения сессий пользователей и их refresh токенов.
 *
 * Таблица: user_management.user_sessions
 *
 * Основные поля:
 * - sessionId: Уникальный ID сессии (первичный ключ)
 * - userId: Внешний ключ на таблицу users
 * - refreshToken: UUID токен для обновления access token
 * - expiresAt: Время истечения refresh token (обычно 7-30 дней)
 * - createdAt: Время создания сессии
 * - revoked: Флаг отзыва (true = токен больше не валиден)
 * - ipAddress: IP-адрес клиента (для аудита)
 * - userAgent: User-Agent браузера (для аудита)
 *
 * Индексы:
 * - idx_user_sessions_user_id: для быстрого поиска сессий пользователя
 * - idx_user_sessions_expires_at: для очистки истекших сессий
 *
 * Уникальные ограничения:
 * - refresh_token должен быть уникален (один токен = одна сессия)
 *
 * @see UserSession доменная модель
 * @see UserSessionPersistenceMapper преобразование Entity ↔ Domain
 */
@Entity
@Table(
        name = "user_sessions",
        schema = "user_management",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "refresh_token")
        },
        indexes = {
                @Index(columnList = "user_id", name = "idx_user_sessions_user_id"),
                @Index(columnList = "expires_at", name = "idx_user_sessions_expires_at")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sessions_seq")
    @SequenceGenerator(
            name = "user_sessions_seq",
            sequenceName = "user_sessions_session_id_seq",
            schema = "user_management",
            allocationSize = 1
    )
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "ip_address", columnDefinition = "inet")
    @Type(PostgreSQLInetType.class)
    private Inet ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

}
