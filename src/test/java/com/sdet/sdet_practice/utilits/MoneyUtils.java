package com.sdet.sdet_practice.utilits;

import java.math.BigDecimal;

public final class MoneyUtils {
    private MoneyUtils() {
    }

    public static BigDecimal parseMoney(String raw) {
        String normalized = raw.replace("$", "").replace(",", "").trim();
        return new BigDecimal(normalized);
    }
}
