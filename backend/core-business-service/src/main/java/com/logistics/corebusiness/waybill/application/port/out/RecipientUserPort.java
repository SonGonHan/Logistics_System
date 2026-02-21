package com.logistics.corebusiness.waybill.application.port.out;

/**
 * Порт для работы с пользователями-получателями.
 *
 * <p>Скрывает детали того, как именно происходит поиск/создание получателя
 * (HTTP к user-auth-service, прямой JPA и т.д.).
 */
public interface RecipientUserPort {
    Long findOrCreateByPhone(String phone);
}