package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
@DisplayName("Интеграционные тесты для UserPersistenceAdapter")
class UserPersistenceAdapterIntegrationTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Test
    @DisplayName("Должен сохранить и получить пользователя")
    void shouldSaveAndRetrieveUser() {
        // Given
        User user = User.builder().email("adapter@test.com").phone("+79993333333").passwordHash("hashed123").firstName("Adapter").lastName("Test").role(UserRole.DISPATCHER).status(UserStatus.ACTIVE).lastAccessedTime(LocalDateTime.now()).build();

        // When
        adapter.save(user);

        // Then
        Optional<User> found = adapter.findByEmail("adapter@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo("+79993333333");
    }
}
