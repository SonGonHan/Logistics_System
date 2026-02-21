package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO для габаритов посылки.
 *
 * <h2>Правило "все или ничего"</h2>
 * Либо все три поля заполнены, либо все null.
 * Валидация на уровне domain при создании Dimensions record.
 */
@Builder
public record DimensionsDto(
        @DecimalMin(value = "0.01", message = "Длина должна быть больше 0")
        BigDecimal length,

        @DecimalMin(value = "0.01", message = "Ширина должна быть больше 0")
        BigDecimal width,

        @DecimalMin(value = "0.01", message = "Высота должна быть больше 0")
        BigDecimal height
) {
}
