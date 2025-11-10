package com.logistics.userauth.auth.session.adapters.out.persistence;

import com.logistics.userauth.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntity, Long> {
    Optional<UserSessionEntity> findByUser(User user);

    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

}
