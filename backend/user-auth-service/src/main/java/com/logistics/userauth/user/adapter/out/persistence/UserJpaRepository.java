package com.logistics.userauth.user.adapter.out.persistence;

import com.logistics.userauth.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA репозиторий для работы с пользователями.
 *
 * <h2>Методы</h2>
 * Наследует от JpaRepository:
 * - save, saveAll, delete, deleteAll, findById, findAll и т.д.
 *
 * Плюс кастомные методы для поиска:
 * - findByEmail(email)
 * - findByPhone(phone)
 * - findByRole(role)
 * - findByFacilityId(id)
 *
 * @see UserEntity для сущности
 * @see UserPersistenceAdapter для использования в бизнес-логике
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);

    Optional<UserEntity> findByRole(UserRole role);

    Optional<UserEntity> findByFacilityId(long id);

}