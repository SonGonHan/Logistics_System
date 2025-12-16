package com.logistics.userauth.auth.session.application.port.out;

import com.logistics.userauth.auth.session.adapter.out.persistence.UserSessionPersistenceAdapter;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapter.out.persistence.UserPersistenceAdapter;
import com.logistics.userauth.user.domain.User;

import java.util.Optional;

/**
 * Порт (интерфейс) для работы с хранилищем сессий.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для всех операций с сессий,
 * не привязываясь к конкретной реализации (JPA, MongoDB и т.д.).
 *
 * <h2>Реализации</h2>
 * - UserSessionPersistenceAdapter (текущая - JPA)
 * - Может быть заменена на другую реализацию при необходимости
 *
 * <h2>Методы</h2>
 * - save(userSession) - Сохранить или обновить сессию
 * - delete(userSession) - Удалить сессию
 * - findById(id) - Найти по ID
 * - findByUser(user) - Найти по пользователю
 * - findByRefreshToken(refreshToken) - Найти по refresh-токену
 *
 * @see UserSessionPersistenceAdapter для реализации на JPA
 * @see UserSession для доменной сущности
 */
public interface UserSessionRepository {
    Optional<UserSession> findByUser(User user);

    Optional<UserSession> findByRefreshToken(String refreshToken);

    void save(UserSession userSession);

    void delete(UserSession userSession);
}
