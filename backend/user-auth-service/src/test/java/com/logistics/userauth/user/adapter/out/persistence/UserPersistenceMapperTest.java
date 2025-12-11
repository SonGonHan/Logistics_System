package com.logistics.userauth.user.adapter.out.persistence;

import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для UserPersistenceMapper")
class UserPersistenceMapperTest {

    private UserPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserPersistenceMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        User domain = User.builder()
                .id(1L)
                .email("test@example.com")
                .phone("+79991234567")
                .passwordHash("hashedPassword123")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .role(UserRole.CLIENT)
                .facilityId(100L)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .lastAccessedTime(LocalDateTime.now())
                .build();

        // When
        UserEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getRole()).isEqualTo(UserRole.CLIENT);
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        UserEntity entity = UserEntity.builder()
                .id(2L)
                .email("courier@example.com")
                .phone("+79997654321")
                .passwordHash("hashedPassword456")
                .firstName("Петр")
                .lastName("Петров")
                .role(UserRole.COURIER)
                .facilityId(200L)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .build();

        // When
        User domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getEmail()).isEqualTo("courier@example.com");
        assertThat(domain.getRole()).isEqualTo(UserRole.COURIER);
    }
}
