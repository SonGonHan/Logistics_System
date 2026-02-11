/**
 * Доменные исключения для подсистемы накладных.
 *
 * <p>Содержит специфичные для бизнес-логики исключения:
 * <ul>
 *   <li><b>DraftNotFoundException</b> - черновик не найден</li>
 *   <li><b>DraftAccessDeniedException</b> - отказ в доступе к черновику</li>
 *   <li><b>DraftInvalidStatusException</b> - недопустимая операция для текущего статуса</li>
 *   <li><b>DraftValidationException</b> - нарушение бизнес-правил валидации</li>
 * </ul>
 *
 * <p>Эти исключения бросаются из application/domain слоев и обрабатываются
 * {@link com.logistics.corebusiness.common.web.GlobalExceptionHandler},
 * который преобразует их в HTTP ответы с соответствующими статус-кодами.
 */
package com.logistics.corebusiness.waybill.application.exception;