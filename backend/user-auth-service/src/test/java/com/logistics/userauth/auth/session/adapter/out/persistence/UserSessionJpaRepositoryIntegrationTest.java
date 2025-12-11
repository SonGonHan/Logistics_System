package com.logistics.userauth.auth.session.adapter.out.persistence;

import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import com.logistics.userauth.user.adapter.out.persistence.UserJpaRepository;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("Интеграционные тесты для UserSessionJpaRepository")
class UserSessionJpaRepositoryIntegrationTest {

    @Autowired
    private UserSessionJpaRepository repository;

    @Autowired
    private UserJpaRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Создаём и сохраняем тестового пользователя
        testUser = UserEntity.builder()
                .phone("+79992222222")
                .firstName("Session")
                .lastName("User")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        testUser = userRepository.save(testUser);
    }

    @Test
    @Transactional
    @DisplayName("Контекст загружается успешно")
    void contextLoads() {
        assertThat(repository).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("Должен сохранить и найти сессию по токену")
    void shouldSaveAndFindBySessionToken() {

        Inet in = new Inet("192.168.1.10");
        // Given
        UserSessionEntity session = UserSessionEntity.builder()
                .user(testUser)
                .sessionToken("unique-token-12345")
                .expiresAt(LocalDateTime.now().plusHours(2))
                .createdAt(LocalDateTime.now())
                .ipAddress(in)
                .userAgent("Mozilla/5.0")
                .build();

        // When
        repository.save(session);

        // Then
        Optional<UserSessionEntity> found = repository.findBySessionToken("unique-token-12345");

        assertThat(found).isPresent();
        assertThat(found.get().getUser()).isEqualTo(testUser);
        assertThat(found.get().getIpAddress()).isEqualTo(in);
        assertThat(found.get().getUserAgent()).isEqualTo("Mozilla/5.0");
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой Optional для несуществующего токена")
    void shouldReturnEmptyForNonExistentToken() {
        // When
        Optional<UserSessionEntity> found = repository.findBySessionToken("non-existent-token");

        // Then
        assertThat(found).isEmpty();
    }
}