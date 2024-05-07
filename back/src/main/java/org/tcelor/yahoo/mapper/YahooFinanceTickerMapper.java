package org.tcelor.yahoo.mapper;

import org.tcelor.yahoo.model.SingleChartMetadata;
import org.tcelor.yahoo.model.YahooFinanceRatioResponse;
import org.tcelor.yahoo.model.YahooFinanceResponse;

public class YahooFinanceTickerMapper {

    public static SingleChartMetadata convertToCustomFormat(YahooFinanceResponse yahooFinanceResponse, String ticker, Boolean accumulating, YahooFinanceRatioResponse ratio) {
        if (yahooFinanceResponse == null || yahooFinanceResponse.chart == null ||
                yahooFinanceResponse.chart.result == null || yahooFinanceResponse.chart.result.isEmpty() ) {
            return null;
        }

        YahooFinanceResponse.YahooFinanceChart.YahooFinanceResult result = yahooFinanceResponse.chart.result.get(0);
        if (result.timestamp == null || result.indicators == null || result.indicators.adjclose == null || result.indicators.adjclose.isEmpty() || result.indicators.quote  == null || result.indicators.quote.isEmpty() ) {
            return null;
        }

        SingleChartMetadata convertedYahooFinance = new SingleChartMetadata();
        convertedYahooFinance.ticker = ticker;
        convertedYahooFinance.timestamp = result.timestamp;
        convertedYahooFinance.value = accumulating ? result.indicators.adjclose.get(0).adjclose
                : result.indicators.quote.get(0).close;
        for (int i = 0; i < convertedYahooFinance.value.size(); i++) {
            if (convertedYahooFinance.value.get(i) == null) {
                if (i == 0) {
                    int j = i + 1;
                    while (j < convertedYahooFinance.value.size() && convertedYahooFinance.value.get(j) == null) {
                        j++;
                    }
                    if (j < convertedYahooFinance.value.size()) {
                        convertedYahooFinance.value.set(i, convertedYahooFinance.value.get(j));
                    } else { //every values are null
                        convertedYahooFinance.value.set(i, 0.0f);
                    }
                } else {
                    convertedYahooFinance.value.set(i, convertedYahooFinance.value.get(i - 1));
                }
            }
        }
        if (ratio.optionChain.result != null && ratio.optionChain.result.size() > 0) {
            convertedYahooFinance.longName = ratio.optionChain.result.get(0).quote.longName;
            if (ratio.optionChain.result.get(0).quote.epsCurrentYear != 0.0f) {
                convertedYahooFinance.pe = ratio.optionChain.result.get(0).quote.regularMarketPrice
                        / ratio.optionChain.result.get(0).quote.epsCurrentYear;
            }
            convertedYahooFinance.dividend = ratio.optionChain.result.get(0).quote.trailingAnnualDividendYield * 100;
            if (ratio.optionChain.result.get(0).quote.bookValue != 0.0f) {
                convertedYahooFinance.roe = ratio.optionChain.result.get(0).quote.epsCurrentYear
                        / ratio.optionChain.result.get(0).quote.bookValue * 100;
            }
        }
        return convertedYahooFinance;
    }
}
