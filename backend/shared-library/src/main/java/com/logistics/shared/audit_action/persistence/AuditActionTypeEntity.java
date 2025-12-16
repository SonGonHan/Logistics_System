package com.logistics.shared.audit_action.persistence;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA сущность для хранения типов аудит-действий в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: shared_data
 * Table: audit_action_types
 *
 * @see AuditActionJpaRepository для работы с БД
 * @see AuditActionTypeMapper для преобразования Domain ↔ Entity
 */
@Entity
@Table(
        name = "audit_action_types",
        schema = "shared_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "action_name")
        },
        indexes = {
//                @Index(columnList = "action_name", name = "audit_action_types_action_name_key")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditActionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_action_type_seq")
    @SequenceGenerator(
            name = "audit_action_type_seq",
            sequenceName = "audit_action_types_action_type_id_seq",
            schema = "shared_data",
            allocationSize = 1
    )
    @Column(name = "action_type_id")
    private Short id;

    @Column(name = "action_name", nullable = false)
    private String actionName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description")
    private String description;

}
