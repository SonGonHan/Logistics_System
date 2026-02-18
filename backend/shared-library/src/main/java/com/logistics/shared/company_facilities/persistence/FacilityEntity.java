package com.logistics.shared.company_facilities.persistence;

import com.logistics.shared.company_facilities.domain.FacilityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * JPA-сущность объекта компании: ПВЗ, склад или офис.
 *
 * <p>Отображается на таблицу {@code shared_data.company_facilities}.
 * Тип объекта хранится в БД как строка и валидируется CHECK-констрейнтом
 * ({@code 'PVZ', 'WAREHOUSE', 'OFFICE'}).
 *
 * <p>Поле {@code closedDate} заполняется при закрытии объекта.
 * Пока оно {@code null} — объект считается действующим.
 *
 * @see FacilityType
 */
@Entity
@Table(
        name = "company_facilities",
        schema = "shared_data"
)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FacilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_seq")
    @SequenceGenerator(
            name = "facility_seq",
            sequenceName = "company_facilities_facility_id_seq",
            schema = "shared_data",
            allocationSize = 1
    )
    @Column(name = "facility_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "facility_type")
    private FacilityType type;

    @Column(name = "facility_name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "closed_date")
    private LocalDate closedDate;
}
