package com.logistics.shared.utils;

public class PhoneUtils {

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }

        // Убираем пробелы, дефисы, круглые скобки
        String cleaned = value.replaceAll("[\\s\\-()]", "");

        // Если номер начинается с '+', применяем правила преобразования
        if (cleaned.startsWith("+")) {
            // Россия/Казахстан: +7XXXXXXXXXX -> 8XXXXXXXXXX
            if (cleaned.startsWith("+7") && cleaned.length() == 12) {
                // "+7" (2 символа) заменяем на "8"
                return "8" + cleaned.substring(2);
            }

            // Беларусь: +375XXXXXXXXX -> 376XXXXXXXXX
            if (cleaned.startsWith("+375") && cleaned.length() == 13) {
                // "+375" (4 символа) заменяем на "376"
                return "376" + cleaned.substring(4);
            }

            // Для прочих случаев просто убираем '+'
            return cleaned.substring(1);
        }

        // Если нет '+', просто возвращаем очищенный номер
        return cleaned;
    }
}
