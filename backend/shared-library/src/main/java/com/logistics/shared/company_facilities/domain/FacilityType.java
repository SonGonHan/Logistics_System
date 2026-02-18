package com.logistics.shared.company_facilities.domain;

/**
 * Тип объекта компании.
 *
 * <p>Значения валидируются CHECK-констрейнтом в таблице
 * {@code shared_data.company_facilities}. Добавление нового типа
 * требует новой Flyway-миграции.
 */
public enum FacilityType {
    PVZ,
    WAREHOUSE,
    OFFICE
}
