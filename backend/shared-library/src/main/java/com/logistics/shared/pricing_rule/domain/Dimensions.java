package com.logistics.shared.pricing_rule.domain;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object, представляющий габариты (длина × ширина × высота) в сантиметрах.
 *
 * <h2>Применение</h2>
 * Используется в {@link PricingRule} для задания предельных (максимально допустимых)
 * габаритов посылки для данного тарифного плана.
 *
 * <h2>Реализация</h2>
 * - Record (неизменяемый)
 * - @Embeddable — сохраняется в трёх отдельных колонках БД
 * - Принцип "все или ничего": либо все null, либо все заполнены
 *
 * @see PricingRule для использования в тарифах
 */
@Embeddable
public record Dimensions(
        BigDecimal length,
        BigDecimal width,
        BigDecimal height
) {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public Dimensions {
        if (length != null && length.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Length must be positive, got: " + length);
        }
        if (width != null && width.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Width must be positive, got: " + width);
        }
        if (height != null && height.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Height must be positive, got: " + height);
        }

        boolean hasNull = length == null || width == null || height == null;
        boolean hasNonNull = length != null || width != null || height != null;
        if (hasNull && hasNonNull) {
            throw new IllegalArgumentException(
                    "All dimensions must be either null or non-null together. Got: length=" + length +
                            ", width=" + width + ", height=" + height
            );
        }
    }

    /**
     * Calculates volume in cubic centimeters (cm³).
     *
     * @return volume in cm³, or null if dimensions are not set
     */
    public BigDecimal calculateVolume() {
        if (length == null) {
            return null;
        }
        return length.multiply(width)
                .multiply(height)
                .setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Checks if any single dimension of a package exceeds this dimension's corresponding side.
     * Useful for validating a package fits within the pricing rule limits.
     *
     * @param other the package dimensions to check against this (max) dimensions
     * @return true if the package exceeds any limit
     */
    public boolean isExceededBy(Dimensions other) {
        if (length == null || other == null || other.length() == null) {
            return false;
        }
        return other.length().compareTo(length) > 0 ||
                other.width().compareTo(width) > 0 ||
                other.height().compareTo(height) > 0;
    }

    public static Dimensions of(BigDecimal length, BigDecimal width, BigDecimal height) {
        return new Dimensions(length, width, height);
    }
}