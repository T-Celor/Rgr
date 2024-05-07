package org.tcelor.yahoo.model;

import java.util.List;

public class YahooFinanceRatioResponse {
    public YahooFinanceOptionChain optionChain;

    public static class YahooFinanceOptionChain {
        public List<YahooFinanceResultOptionChain> result;

        public static class YahooFinanceResultOptionChain {
            public YahooFinanceQuote quote;

            public static class YahooFinanceQuote {
                public String longName;
                public float regularMarketPrice;
                public float trailingAnnualDividendYield;
                public float epsCurrentYear;
                public float bookValue;
            }
        }
    }
}
