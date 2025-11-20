package com.logistics.shared.audit_action.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
public class AuditActionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "action_type_id")
    private Short id;

    @Column(name = "action_name", nullable = false)
    private String actionName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description")
    private String description;

}
