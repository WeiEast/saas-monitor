package com.treefinance.saas.monitor.common.utils;

import java.math.BigDecimal;

/**
 * @author chengtong
 * @date 18/3/15 17:37
 */
public class StatisticCalcUtil {
    /**
     * @param numerator 分子
     * @param denominator 分母
     *
     * @return a*100/b
     */
    public static BigDecimal calcRate(Integer numerator, Integer denominator) {
        if (Integer.valueOf(0).compareTo(denominator) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator, 2), 2, BigDecimal.ROUND_HALF_UP);
    }


    public static BigDecimal calcRatio(Integer a, Integer b) {
        if (Integer.valueOf(0).compareTo(a) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = BigDecimal.valueOf(b, 1)
                .divide(BigDecimal.valueOf(a, 1), 1, BigDecimal.ROUND_HALF_UP);
        return rate;
    }


}
