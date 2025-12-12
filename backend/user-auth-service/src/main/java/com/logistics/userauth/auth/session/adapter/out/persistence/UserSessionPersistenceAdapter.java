package com.logistics.userauth.auth.session.adapter.out.persistence;

import com.logistics.userauth.auth.session.application.ports.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
