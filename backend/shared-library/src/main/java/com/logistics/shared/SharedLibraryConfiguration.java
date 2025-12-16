package com.logistics.shared;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Конфигурация Spring для shared-library модуля.
 *
 * <h2>Назначение</h2>
 * Регистрирует бины и конфигурирует компоненты shared-library,
 * которые будут использоваться другими микросервисами.
 *
 * <h2>Использование в других сервисах</h2>
 * <pre>
 * \\@SpringBootApplication
 * \\@ComponentScan(basePackages = {
 *     \"com.myservice\",
 *     \"com.logistics.shared\"
 * })
 * \\@EnableJpaRepositories(basePackages = \"com.logistics.shared\")
 * public class MyServiceApplication { }
 * </pre>
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Configuration
@EntityScan("com.logistics.shared")
@EnableJpaRepositories("com.logistics.shared")
public class SharedLibraryConfiguration {

}