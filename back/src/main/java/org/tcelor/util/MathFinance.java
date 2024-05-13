package org.tcelor.util;

import java.util.List;

public class MathFinance {
    // Calculate Standard Deviation
    public static Float calculateStandardDeviation(List<Float> values) {
        if (values == null || values.size() < 2) {
            return 0.0f;
        }

        float sum = 0.0f;
        float mean = calculateMean(values);

        for (float value : values) {
            sum += Math.pow(value - mean, 2);
        }

        float variance = sum / (values.size() - 1);
        return (float) Math.sqrt(variance);
    }

    // Calculate Maximum Drawdown
    public static Float calculateMaxDrawdown(List<Float> values) {
        if (values == null || values.size() < 2) {
            return 0.0f;
        }

        float maxDrawdown = 0.0f;
        float peak = values.get(0);

        for (float value : values) {
            if (value > peak) {
                peak = value;
            } else {
                float drawdown = (peak - value) / peak;
                maxDrawdown = Math.max(maxDrawdown, drawdown);
            }
        }

        return maxDrawdown;
    }

    // Calculate Compound Annual Growth Rate (CAGR)
    public static Float calculateCAGR(List<Float> values, int years) {
        if (values == null || values.isEmpty() || years <= 0) {
            return 0.0f;
        }

        float startValue = values.get(0);
        float endValue = values.get(values.size() - 1);

        return (float) (Math.pow(endValue / startValue, 1.0 / years) - 1.0);
    }

    // Calculate Sharpe Ratio
    public static Float calculateSharpeRatio(float cagr, float riskFreeRate, float std) {
        return (cagr - riskFreeRate) / std;
    }

    // Calculate MDD / CAGR
    public static Float calculateRecoveryMDD(float cagr, float mdd) {
        return (float) (Math.log(mdd / 100 + 1) / Math.log(cagr / 100 + 1));
    }

    // Calculate Variance
    public static float calculateVariance(List<Float> prices) {
        if (prices == null || prices.size() < 2) {
            return 0.0f;
        }

        float sum = 0.0f;
        float mean = calculateMean(prices);

        for (float price : prices) {
            float diff = price - mean;
            sum += diff * diff;
        }

        return sum / (prices.size()) / 100;
    }

    // Calculate Mean
    public static float calculateMean(List<Float> prices) {
        float sum = 0.0f;
        for (float price : prices) {
            sum += price;
        }
        return sum / prices.size();
    }

    // Calculate Monthly Volatility (Standard Deviation of Returns)
    public static float calculateMonthlyVolatility(List<Float> prices) {
        float variance = calculateVariance(prices);
        return (float) Math.sqrt(variance) / prices.get(prices.size() - 1) * 100;
    }

    // Calculate Annualized Volatility
    public static float calculateAnnualizedVolatility(List<Float> prices) {
        float variance = (float) calculateVariance(prices);
        return (float) Math.sqrt(variance * 12) / prices.get(prices.size() - 1) * 100;
    }
}
