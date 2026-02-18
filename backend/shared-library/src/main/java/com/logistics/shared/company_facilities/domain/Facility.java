package com.logistics.shared.company_facilities.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Доменный объект, представляющий физический объект компании.
 *
 * <p>Объект считается <b>активным</b>, пока {@code closedDate} равен {@code null}
 * или дата закрытия ещё не наступила. Проверку выполняет
 * {@link com.logistics.shared.company_facilities.FacilityService#isActive(Facility)}.
 *
 * <p>Не содержит JPA-аннотаций — чистый доменный объект.
 *
 * @see FacilityType
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Facility {

    private Long id;

    private FacilityType type;

    private String name;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime createdAt;

    private LocalDate closedDate;
}
