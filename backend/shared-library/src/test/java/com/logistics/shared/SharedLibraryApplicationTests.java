package com.logistics.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Shared Library: загрузка контекста")
class SharedLibraryApplicationTests {

    @Test
    @DisplayName("Должен успешно загрузить контекст Spring")
    void contextLoads() {
        // Этот тест проверяет, что все бины и конфигурации загружаются без ошибок
    }
}
