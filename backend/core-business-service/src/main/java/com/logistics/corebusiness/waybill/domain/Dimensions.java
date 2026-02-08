package com.logistics.corebusiness.waybill.domain;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object representing package dimensions (length x width x height).
 * Immutable and contains business logic for volume and volumetric weight calculations.
 * <p>
 * Mapped as @Embeddable to three separate database columns for SQL queryability.
 */
@Embeddable
public record Dimensions(
        BigDecimal length,
        BigDecimal width,
        BigDecimal height
) {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Compact constructor with validation.
     * Ensures all dimensions are positive or all are null (for nullable cases).
     */
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

        // Ensure all-or-nothing: either all null or all non-null
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
     * Calculates package volume in cubic centimeters (cm³).
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
     * Calculates volumetric weight based on a divisor.
     * Commonly used in logistics: volumetric_weight = volume / divisor
     * <p>
     * Typical divisors:
     * - 5000 for international shipping
     * - 6000 for domestic shipping
     *
     * @param divisor the divisor to calculate volumetric weight (e.g., 5000)
     * @return volumetric weight in kg, or null if dimensions are not set
     */
    public BigDecimal calculateVolumetricWeight(int divisor) {
        BigDecimal volume = calculateVolume();
        if (volume == null) {
            return null;
        }
        return volume.divide(BigDecimal.valueOf(divisor), SCALE, ROUNDING_MODE);
    }

    /**
     * Formats dimensions as a human-readable string.
     *
     * @return formatted string like "30x40x50 см", or null if dimensions are not set
     */
    public String format() {
        if (length == null) {
            return null;
        }
        return String.format("%sx%sx%s см", length, width, height);
    }

    /**
     * Checks if any dimension exceeds the given maximum.
     * Useful for validating against pricing rule limits (future Scenario C).
     *
     * @param maxDimension maximum allowed dimension in cm
     * @return true if any dimension exceeds the limit
     */
    public boolean exceedsMaxDimension(BigDecimal maxDimension) {
        if (length == null) {
            return false;
        }
        return length.compareTo(maxDimension) > 0 ||
                width.compareTo(maxDimension) > 0 ||
                height.compareTo(maxDimension) > 0;
    }

    /**
     * Factory method for creating Dimensions from three values.
     *
     * @param length in cm
     * @param width  in cm
     * @param height in cm
     * @return new Dimensions instance
     */
    public static Dimensions of(BigDecimal length, BigDecimal width, BigDecimal height) {
        return new Dimensions(length, width, height);
    }
}
