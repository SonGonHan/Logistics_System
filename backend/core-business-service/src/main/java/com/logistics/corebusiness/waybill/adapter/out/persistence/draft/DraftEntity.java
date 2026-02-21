package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA сущность для хранения черновиков накладных в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: waybill_service
 * Table: waybill_drafts
 *
 * Уникальность: barcode UNIQUE
 * Индексы: barcode, draft_creator_id, sender_user_id, recipient_user_id, draft_status, created_at
 *
 * <h2>Важные особенности</h2>
 * - Вес и габариты посылки не хранятся: они определяются выбранным тарифным планом (pricing_rule_id)
 * - draft_status маппится через @Enumerated(STRING) с CHECK constraint в БД
 *
 * @see DraftJpaRepository для работы с БД
 * @see DraftPersistenceMapper для преобразования Domain ↔ Entity
 * @see Draft для доменной модели
 */
@Entity
@Table(
        name = "waybill_drafts",
        schema = "waybill_service",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "barcode")
        },
        indexes = {
                @Index(columnList = "barcode", name = "idx_drafts_barcode"),
                @Index(columnList = "draft_creator_id", name = "idx_drafts_creator"),
                @Index(columnList = "sender_user_id", name = "idx_drafts_sender"),
                @Index(columnList = "recipient_user_id", name = "idx_drafts_recipient"),
                @Index(columnList = "draft_status", name = "idx_drafts_status"),
                @Index(columnList = "created_at", name = "idx_drafts_created_at")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DraftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "draft_seq")
    @SequenceGenerator(
            name = "draft_seq",
            sequenceName = "waybill_drafts_draft_id_seq",
            schema = "waybill_service",
            allocationSize = 1
    )
    @Column(name = "draft_id")
    private Long id;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    @Column(name = "draft_creator_id", nullable = false)
    private Long draftCreatorId;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Column(name = "recipient_address", nullable = false)
    private String recipientAddress;

    @Column(name = "pricing_rule_id")
    private Long pricingRuleId;

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "draft_status", nullable = false)
    private DraftStatus draftStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}