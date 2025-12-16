package com.logistics.shared.audit_action.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuditActionTypeMapper: юнит-тесты")
class AuditActionTypeMapperTest {

    private AuditActionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuditActionTypeMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        AuditActionTypeEntity entity = AuditActionTypeEntity.builder()
                .id((short) 1)
                .actionName("USER_LOGIN")
                .category("AUTHENTICATION")
                .description("Пользователь вошел в систему")
                .build();

        // When
        AuditActionType domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo((short) 1);
        assertThat(domain.getActionName()).isEqualTo("USER_LOGIN");
        assertThat(domain.getCategory()).isEqualTo("AUTHENTICATION");
        assertThat(domain.getDescription()).isEqualTo("Пользователь вошел в систему");
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        AuditActionType domain = AuditActionType.builder()
                .id((short) 2)
                .actionName("USER_LOGOUT")
                .category("AUTHENTICATION")
                .description("Пользователь вышел из системы")
                .build();

        // When
        AuditActionTypeEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo((short) 2);
        assertThat(entity.getActionName()).isEqualTo("USER_LOGOUT");
        assertThat(entity.getCategory()).isEqualTo("AUTHENTICATION");
        assertThat(entity.getDescription()).isEqualTo("Пользователь вышел из системы");
    }
}
