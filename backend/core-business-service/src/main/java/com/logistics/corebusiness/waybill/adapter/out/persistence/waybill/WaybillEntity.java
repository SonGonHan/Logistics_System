package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA сущность для хранения накладных в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: waybill_service
 * Table: waybills
 *
 * Уникальность: waybill_number UNIQUE
 * Индексы: waybill_number, waybill_creator_id, sender_user_id, recipient_user_id, waybill_status, created_at
 *
 * <h2>Важные особенности</h2>
 * - Вес и габариты посылки не хранятся: они определяются выбранным тарифным планом (pricing_rule_id)
 * - waybill_status маппится через @Enumerated(STRING) с CHECK constraint в БД
 *
 * @see WaybillJpaRepository для работы с БД
 * @see WaybillPersistenceMapper для преобразования Domain ↔ Entity
 * @see Waybill для доменной модели
 */
@Entity
@Table(
        name = "waybills",
        schema = "waybill_service",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "waybill_number")
        },
        indexes = {
                @Index(columnList = "waybill_number", name = "idx_waybills_number"),
                @Index(columnList = "waybill_creator_id", name = "idx_waybills_creator"),
                @Index(columnList = "sender_user_id", name = "idx_waybills_sender"),
                @Index(columnList = "recipient_user_id", name = "idx_waybills_recipient"),
                @Index(columnList = "waybill_status", name = "idx_waybills_status"),
                @Index(columnList = "created_at", name = "idx_waybills_created_at")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaybillEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "waybill_seq")
        @SequenceGenerator(
                name = "waybill_seq",
                sequenceName = "waybills_waybill_id_seq",
                schema = "waybill_service",
                allocationSize = 1
        )
        @Column(name = "waybill_id")
        private Long id;

        @Column(name = "waybill_number", nullable = false)
        private String waybillNumber;

        @Column(name = "waybill_creator_id", nullable = false)
        private Long waybillCreatorId;

        @Column(name = "sender_user_id", nullable = false)
        private Long senderUserId;

        @Column(name = "recipient_user_id", nullable = false)
        private Long recipientUserId;

        @Column(name = "recipient_address", nullable = false)
        private String recipientAddress;

        @Column(name = "pricing_rule_id")
        private Long pricingRuleId;

        @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
        private BigDecimal finalPrice;

        @Enumerated(EnumType.STRING)
        @Column(name = "waybill_status", nullable = false)
        private WaybillStatus status;

        @CreatedDate
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "accepted_at")
        private LocalDateTime acceptedAt;
}