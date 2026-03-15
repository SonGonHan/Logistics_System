package com.logistics.corebusiness.waybill.application.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Генератор уникальных номеров накладных.
 *
 * <h2>Формат</h2>
 * WB-YYMMDD-XXXXXX
 * - WB: префикс "Waybill"
 * - YYMMDD: дата создания
 * - XXXXXX: 6 случайных цифр
 *
 * <h2>Примеры</h2>
 * - WB-260314-123456
 * - WB-260314-654321
 *
 * <h2>Уникальность</h2>
 * Комбинация даты и случайной 6-значной части даёт до 1 000 000 значений в день.
 */
@Component
public class WaybillNumberGenerator {

    private static final String PREFIX = "WB";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Генерирует новый номер накладной.
     *
     * @return строка формата WB-YYMMDD-XXXXXX
     */
    public String generate() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = String.format("%06d", RANDOM.nextInt(1_000_000));
        return PREFIX + "-" + datePart + "-" + randomPart;
    }
}
