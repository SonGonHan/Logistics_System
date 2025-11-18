package com.logistics.userauth.user.adapters.out.persistence;

import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("Интеграционные тесты для UserJpaRepository")
class UserJpaRepositoryIntegrationTest {

    @Autowired
    private UserJpaRepository repository;

    @Test
    @DisplayName("Контекст загружается успешно")
    void contextLoads() {
        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("Должен сохранить и найти User по ID")
    void shouldSaveAndFindById() {
        // Given
        UserEntity entity = UserEntity.builder()
                .email("test@example.com")
                .phone("1234567890")
                .passwordHash("hashedpassword")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.CLIENT)
                .build();

        UserEntity savedEntity = repository.save(entity);

        // When
        Optional<UserEntity> found = repository.findById(savedEntity.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getId()).isEqualTo(savedEntity.getId());
    }
}
