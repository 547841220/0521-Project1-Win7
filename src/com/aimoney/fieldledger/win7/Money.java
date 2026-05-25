package com.aimoney.fieldledger.win7;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

final class Money {
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.##");

    private Money() {
    }

    static long yuanToCents(String value) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            return 0L;
        }
        return new BigDecimal(text).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    static long amount(double quantity, long unitPriceCents) {
        return BigDecimal.valueOf(quantity)
            .multiply(BigDecimal.valueOf(unitPriceCents))
            .setScale(0, RoundingMode.HALF_UP)
            .longValue();
    }

    static String centsToYuan(long cents) {
        return MONEY_FORMAT.format(BigDecimal.valueOf(cents).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
    }

    static String centsInput(long cents) {
        return BigDecimal.valueOf(cents).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).toPlainString();
    }

    static double parseDouble(String value) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            return 0D;
        }
        return Double.parseDouble(text);
    }

    static int parseInt(String value) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    static String number(double value) {
        return NUMBER_FORMAT.format(value);
    }
}
