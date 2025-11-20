package com.logistics.userauth.auth.session.application.ports.out;

import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.domain.User;

import java.util.Optional;

public interface UserSessionRepository {
    Optional<UserSession> findByUser(User user);

    Optional<UserSession> findBySessionToken(String sessionToken);

    void save(UserSession userSession);

    void delete(UserSession userSession);
}
