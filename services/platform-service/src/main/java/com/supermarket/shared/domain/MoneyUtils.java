package com.supermarket.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    private static final int SCALE = 4;

    private MoneyUtils() {
    }

    public static BigDecimal scale(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return scale(a.multiply(b));
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return scale(a.add(b));
    }

    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return scale(a.subtract(b));
    }
}
