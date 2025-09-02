package com.cathay.inteview.tutqq.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class RateCalculationUtils {

    private RateCalculationUtils() {} // utility class

    public static BigDecimal calculateMidRate(BigDecimal bidAvg, BigDecimal askAvg) {
        return bidAvg.add(askAvg)
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateSpread(BigDecimal bidAvg, BigDecimal askAvg) {
        return askAvg.subtract(bidAvg);
    }
}
