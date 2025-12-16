package com.logistics.userauth.auth.session.adapter.out.persistence;

import com.logistics.userauth.auth.session.application.port.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.adapter.out.persistence.UserJpaRepository;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Адаптер, реализующий интерфейс UserSessionRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс UserSessionRepository определяет контракт
 * - UserSessionPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * @implements UserSessionRepository
 * @see UserSessionRepository для контракта
 * @see UserSessionJpaRepository для JPA работы
 */
@Component
@RequiredArgsConstructor
public class UserSessionPersistenceAdapter implements UserSessionRepository {

    private final UserSessionJpaRepository jpaRepo;
    private final UserSessionPersistenceMapper mapper;

    @Override
    public Optional<UserSession> findByUser(User user) {
        return jpaRepo.findByUser(user).map(mapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByRefreshToken(String refreshToken) {
        return jpaRepo.findByRefreshToken(refreshToken).map(mapper::toDomain);
    }

    @Override
    public void save(UserSession userSession) {
        UserSessionEntity entity = mapper.toEntity(userSession);
        jpaRepo.save(entity);
    }

    @Override
    public void delete(UserSession userSession) {
        UserSessionEntity entity = mapper.toEntity(userSession);
        jpaRepo.delete(entity);
    }
}
