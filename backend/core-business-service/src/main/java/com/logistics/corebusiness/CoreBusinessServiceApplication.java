package com.logistics.corebusiness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Приложение Core Business Service
 * <p>
 * Обрабатывает основную бизнес-логику: накладные, ценообразование, приёмка, рейтинги
 * <p>
 * Ограниченные контексты (Bounded Contexts):
 * - waybill: Управление жизненным циклом накладных
 * - acceptance: Приёмка посылок в пунктах отправления
 * - rating: Сбор отзывов клиентов
 */
@SpringBootApplication(scanBasePackages = {
    "com.logistics.corebusiness",
    "com.logistics.shared"
})
@EnableJpaRepositories(basePackages = {
    "com.logistics.corebusiness.**.persistence",
    "com.logistics.shared.**.persistence"
})
@EntityScan(basePackages = {
    "com.logistics.corebusiness.**.persistence",
    "com.logistics.shared.**.persistence"
})
@EnableJpaAuditing
public class CoreBusinessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreBusinessServiceApplication.class, args);
    }
}
