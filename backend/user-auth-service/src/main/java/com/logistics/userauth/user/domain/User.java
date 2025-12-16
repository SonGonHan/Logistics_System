package com.logistics.userauth.user.domain;

import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
/**
 * Доменная сущность пользователя системы логистики.
 *
 * <h2>Назначение</h2>
 * Представляет пользователя с его основной информацией:
 * - Личные данные (ФИО)
 * - Контактные данные (email, телефон)
 * - Аутентификация (пароль в хэшированном виде - НИКОГДА не сырой!)
 * - Роль в системе (CLIENT, COURIER, ADMIN и т.д.)
 * - Статус (ACTIVE, ON_DELETE)
 * - Связь с объектом (склад, ПВЗ)
 *
 * <h2>Примеры ролей</h2>
 * - CLIENT: Обычный клиент, заказывающий доставку
 * - COURIER: Курьер (доставка в пределах города)
 * - DRIVER: Водитель (доставка между городами)
 * - PVZ_OPERATOR: Оператор на стойке выдачи
 * - WAREHOUSE_OPERATOR: Оператор склада (комплектует заказы)
 * - DISPATCHER: Диспетчер (управляет маршрутами)
 * - SYSTEM_ADMIN: Администратор системы
 *
 * @see UserRole для доступных ролей
 * @see UserStatus для доступных статусов
 * @see UserEntity для JPA representation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String email;

    private String phone;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private String middleName;

    private UserRole role;

    private Long facilityId;

    private LocalDateTime createdTime;

    private LocalDateTime lastAccessedTime;

    private UserStatus status;

}
