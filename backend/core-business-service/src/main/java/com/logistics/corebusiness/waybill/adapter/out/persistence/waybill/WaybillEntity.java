package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA сущность для таблицы waybill_service.waybills.
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

        @Column(name = "weight_actual", nullable = false, precision = 8, scale = 2)
        private BigDecimal weightActual;

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "length", column = @Column(name = "length_cm", precision = 8, scale = 2)),
                @AttributeOverride(name = "width", column = @Column(name = "width_cm", precision = 8, scale = 2)),
                @AttributeOverride(name = "height", column = @Column(name = "height_cm", precision = 8, scale = 2))
        })
        private Dimensions dimensions;

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
