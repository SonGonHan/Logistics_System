package com.logistics.corebusiness.waybill.application.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Генератор уникальных штрих-кодов для черновиков накладных.
 *
 * <h2>Формат</h2>
 * DRF-YYMMDD-XXXXXX
 * - DRF: Префикс "Draft"
 * - YYMMDD: Дата создания (год, месяц, день)
 * - XXXXXX: 6 случайных цифр
 *
 * <h2>Примеры</h2>
 * - DRF-260209-123456
 * - DRF-260209-789012
 *
 * <h2>Уникальность</h2>
 * Комбинация даты + 6 случайных цифр дает 1,000,000 вариантов в день.
 * Коллизии маловероятны при умеренной нагрузке.
 * В будущем можно добавить проверку уникальности в БД.
 */
@Component
public class BarcodeGenerator {

    private static final String PREFIX = "DRF";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Генерирует новый уникальный barcode.
     *
     * @return строка формата DRF-YYMMDD-XXXXXX
     */
    public String generate() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = String.format("%06d", RANDOM.nextInt(1_000_000));
        return PREFIX + "-" + datePart + "-" + randomPart;
    }
}
