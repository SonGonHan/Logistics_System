package com.logistics.userauth.auth.session.adapters.out.persistence;

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
    public Optional<UserSession> findBySessionToken(String sessionToken) {
        return jpaRepo.findBySessionToken(sessionToken).map(mapper::toDomain);
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
