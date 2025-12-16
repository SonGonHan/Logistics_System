package com.logistics.userauth.audit.adapter.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLInetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * JPA сущность для хранения логов аудита в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: user_management
 * Table: audit_logs
 *
 * <h2>Структура</h2>
 * - audit_log_id: Первичный ключ
 * - user_id: Внешний ключ на users
 * - action_type_id: Внешний ключ на shared_data.audit_action_types
 * - table_name: Названи таблицы, которая была изменена
 * - record_id: ID записи в этой таблице
 * - actor_identifier: Идентификатор актора (email/телефон)
 * - new_values: JSONB с новыми значениями
 * - performed_at: Дата/время действия
 * - ip_address: inet тип (специальный тип PostgreSQL)
 *
 * @see AuditLogJpaRepository для работы с БД
 * @see AuditLogPersistenceMapper для преобразования Domain ↔ Entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "audit_logs",
        schema = "user_management"
        ,
        indexes = {
                @Index(columnList = "user_id", name = "idx_audit_logs_user_id"),
                @Index(columnList = "action_type_id", name = "idx_audit_logs_action_type_id"),
                @Index(columnList = "table_name, record_id", name = "idx_audit_logs_record"),
                @Index(columnList = "performed_at", name = "idx_audit_logs_performed_at")
        }
)
@Builder
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_logs_seq")
    @SequenceGenerator(
            name = "audit_logs_seq",
            sequenceName = "audit_logs_audit_log_id_seq",
            schema = "user_management",
            allocationSize = 1
    )
    @Column(name = "audit_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id")
    private AuditActionTypeEntity actionType;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "actor_identifier")
    private String actorIdentifier;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;

    @CreatedDate
    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "ip_address", columnDefinition = "inet")
    @Type(PostgreSQLInetType.class)
    private Inet ipAddress;
}
