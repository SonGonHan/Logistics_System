package com.logistics.userauth.user.domain;

import com.logistics.userauth.user.infrastructure.LogisticsUserDetails;

/**
 * Перечисление ролей пользователей в системе логистики.
 *
 * <h2>Роли и их значение</h2>
 *
 * Клиенты:
 * - CLIENT: Обычный клиент, заказывающий доставку
 *
 * ПВЗ (Пункт выдачи):
 * - PVZ_OPERATOR: Оператор на стойке выдачи
 * - PVZ_ADMIN: Администратор ПВЗ
 *
 * Доставка:
 * - COURIER: Курьер (доставка в пределах города)
 * - DRIVER: Водитель (доставка между городами)
 *
 * Логистика:
 * - DISPATCHER: Диспетчер (управляет маршрутами и доставками)
 *
 * Склады:
 * - WAREHOUSE_OPERATOR: Оператор склада (комплектует)
 * - WAREHOUSE_ADMIN: Администратор склада
 *
 * Администрация:
 * - HR: HR менеджер
 * - ACCOUNTANT: Бухгалтер
 * - SYSTEM_ADMIN: Администратор системы (полные права)
 * - SYSTEM: Специальная роль для системных операций
 * - UNREGISTERED_CONTACT: Контакт, еще не зарегистрировавшийся
 *
 * <h2>Spring Security интеграция</h2>
 * Преобразуется в ROLE_COURIER, ROLE_CLIENT и т.д.
 * для использования в @PreAuthorize и других security аннотациях.
 *
 * @see User для сущности пользователя
 * @see LogisticsUserDetails для Spring Security интеграции
 */
public enum UserRole {
    UNREGISTERED_CONTACT,
    CLIENT,
    PVZ_OPERATOR, PVZ_ADMIN,
    COURIER, DRIVER,
    DISPATCHER,
    WAREHOUSE_OPERATOR, WAREHOUSE_ADMIN,
    HR, ACCOUNTANT, SYSTEM_ADMIN,
    SYSTEM
}
