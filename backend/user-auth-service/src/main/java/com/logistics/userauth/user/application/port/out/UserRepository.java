package com.logistics.userauth.user.application.port.out;

import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    void delete(User user);

    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    Optional<User> findByRole(UserRole role);

    Optional<User> findByFacilityId(long id);

}
