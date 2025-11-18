package com.logistics.shared.audit_action.persistence;

import com.logistics.shared.IntegrationTest;
import com.logistics.shared.TestApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("Интеграционные тесты для AuditActionJpaRepository")
//@Disabled("Mockito не поддерживает Java 25 полностью. Нужно обновить версию Mockito.")
class AuditActionJpaRepositoryIntegrationTest {

    @Autowired
    private AuditActionJpaRepository repository;

    @Test
    @DisplayName("Должен сохранить и найти AuditActionType по ID")
    void shouldSaveAndFindById() {
        // Given
        // Создаем сущность, которую собираемся сохранить
        AuditActionTypeEntity entityToSave = AuditActionTypeEntity.builder()
                .actionName("ORDER_CREATED")
                .category("ORDER_MANAGEMENT")
                .description("Заказ создан")
                .build();

        // Сохраняем и, что КЛЮЧЕВОЕ, получаем обратно экземпляр с ID
        AuditActionTypeEntity savedEntity = repository.save(entityToSave);

        // When
        // Ищем в базе по ID, полученному от сохраненной сущности
        Optional<AuditActionTypeEntity> found = repository.findById(Integer.valueOf(savedEntity.getId()));

        // Then
        assertThat(found).isPresent(); // Проверяем, что сущность нашлась
        assertThat(found.get().getActionName()).isEqualTo("ORDER_CREATED"); // Проверяем ее содержимое
        assertThat(found.get().getId()).isEqualTo(savedEntity.getId());
    }

}
