package com.logistics.userauth.user.application.port.out;

import com.logistics.userauth.user.adapter.out.persistence.UserPersistenceAdapter;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;

import java.util.Optional;
/**
 * Порт (интерфейс) для работы с хранилищем пользователей.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для всех операций с пользователями,
 * не привязываясь к конкретной реализации (JPA, MongoDB и т.д.).
 *
 * <h2>Реализации</h2>
 * - UserPersistenceAdapter (текущая - JPA)
 * - Может быть заменена на другую реализацию при необходимости
 *
 * <h2>Методы</h2>
 * - save(user) - Сохранить или обновить пользователя
 * - delete(user) - Удалить пользователя
 * - findById(id) - Найти по ID
 * - findByPhone(phone) - Найти по телефону (уникален)
 * - findByEmail(email) - Найти по email
 * - findByRole(role) - Найти первого пользователя с ролью
 * - findByFacilityId(id) - Найти по объекту (склад, ПВЗ)
 *
 * @see UserPersistenceAdapter для реализации на JPA
 * @see User для доменной сущности
 */
public interface UserRepository {
    User save(User user);

    void delete(User user);

    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByRole(UserRole role);

    Optional<User> findByFacilityId(long id);

}
