package com.logistics.userauth.user.adapter.out.persistence;

import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Адаптер, реализующий интерфейс UserRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс UserRepository определяет контракт
 * - UserPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * @implements UserRepository
 * @see UserRepository для контракта
 * @see UserJpaRepository для JPA работы
 */
@RequiredArgsConstructor
@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepo;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        var saved = jpaRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepo.delete(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaRepo.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByRole(UserRole role) {
        return jpaRepo.findByRole(role).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByFacilityId(long id) {
        return jpaRepo.findByFacilityId(id).map(mapper::toDomain);
    }


}
