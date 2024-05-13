package org.tcelor.wallet.model;

import java.util.List;

import org.tcelor.util.MathFinance;

public class ChartMetadata {
    public String longName;
    public Float startValue = 0.0f;
    public Float endValue = 0.0f;
    public Float cagr = 0.0f;
    public Float volatility = 0.0f;
    public Float drawdown = 0.0f;
    public Float sharpeRatio = 0.0f;
    public Float recoveryRatio = 0.0f;

    public float dividend = 0.0f;
    public float pe = 0.0f;
    public float roe = 0.0f;

    public List<Long> timestamp;
    public List<Float> value;

    public ChartMetadata buildMetadata() {
        startValue = value.get(0);
        endValue = value.get(value.size() - 1);
        volatility = MathFinance.calculateAnnualizedVolatility(value);
        cagr = MathFinance.calculateCAGR(value, value.size() / 12) * 100;
        drawdown = MathFinance.calculateMaxDrawdown(value) * 100;
        recoveryRatio = MathFinance.calculateRecoveryMDD(cagr, drawdown);
        sharpeRatio = MathFinance.calculateSharpeRatio(cagr, 0.0f, volatility); //For now FreeRate is 0%
        return this;
    }
}
