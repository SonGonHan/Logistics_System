package com.logistics.corebusiness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Приложение Core Business Service
 * <p>
 * Обрабатывает основную бизнес-логику: накладные, ценообразование, приёмка, рейтинги
 * <p>
 * Ограниченные контексты (Bounded Contexts):
 * - waybill: Управление жизненным циклом накладных
 * - pricing: Расчёт цен и тарифов
 * - acceptance: Приёмка посылок в пунктах отправления
 * - rating: Сбор отзывов клиентов
 */
@SpringBootApplication(scanBasePackages = {
    "com.logistics.corebusiness",
    "com.logistics.shared"  // Shared library components
})
@EnableJpaAuditing
public class CoreBusinessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreBusinessServiceApplication.class, args);
    }
}
