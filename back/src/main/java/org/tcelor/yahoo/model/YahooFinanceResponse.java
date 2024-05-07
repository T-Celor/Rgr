package org.tcelor.yahoo.model;

import java.util.List;

public class YahooFinanceResponse {
    public YahooFinanceChart chart;

    public static class YahooFinanceChart {
        public List<YahooFinanceResult> result;

        public static class YahooFinanceResult {
            public List<Long> timestamp;

            public YahooFinanceIndicators indicators;

            public static class YahooFinanceIndicators {
                public List<YahooFinanceAdjClose> adjclose;
                public List<YahooFinanceQuote> quote;

                public static class YahooFinanceAdjClose {
                    public List<Float> adjclose;
                }

                public static class YahooFinanceQuote {
                    public List<Float> close;
                }
            }
        }
    }
}