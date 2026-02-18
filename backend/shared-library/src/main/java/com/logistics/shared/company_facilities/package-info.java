/**
 * Контекст объектов компании (ПВЗ, склады, офисы).
 *
 * <p>Публичная точка входа — {@link com.logistics.shared.company_facilities.FacilityService}.
 * Внутренние детали (Entity, JPA-репозиторий, Mapper) скрыты в пакете {@code persistence}.
 *
 * <p>Используется в {@code core-business-service} и других сервисах,
 * которым нужна информация о физических объектах компании.
 */
package com.logistics.shared.company_facilities;