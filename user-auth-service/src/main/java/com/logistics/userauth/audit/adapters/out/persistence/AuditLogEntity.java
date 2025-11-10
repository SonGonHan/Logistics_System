package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapters.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "audit_logs",
        schema = "user_management",
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String ipAddress;
}
