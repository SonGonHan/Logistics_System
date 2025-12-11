package com.logistics.userauth.user.adapter.out.persistence;

import com.logistics.userauth.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);

    Optional<UserEntity> findByRole(UserRole role);

    Optional<UserEntity> findByFacilityId(long id);

}