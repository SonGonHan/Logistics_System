package com.logistics.shared;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger для всех микросервисов.
 *
 * <h2>Назначение</h2>
 * Предоставляет единую конфигурацию документации API для всех сервисов.
 * Каждый микросервис импортирует эту конфигурацию через @Import,
 * чтобы иметь единообразный вид Swagger UI.
 *
 * <h2>Использование</h2>
 * <pre>
 * \\@SpringBootApplication
 * \\@Import(OpenApiConfig.class)
 * public class UserAuthServiceApplication { }
 * </pre>
 *
 * После импорта Swagger UI будет доступен по:
 * http://localhost:8080/api/v1/swagger-ui.html
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Logistics System API")
                        .version("1.0.0")
                        .description("REST API для системы логистики и управления доставкой")
                        .contact(new Contact()
                                .name("Logistics Development Team")
                                .email("dev@logistics.com")
                                .url("https://logistics.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token для аутентификации. Вставьте токен без 'Bearer ' префикса.")));
    }
}
