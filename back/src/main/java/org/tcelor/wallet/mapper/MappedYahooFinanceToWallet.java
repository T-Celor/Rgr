package org.tcelor.wallet.mapper;

import java.util.ArrayList;
import java.util.List;

import org.tcelor.wallet.model.ChartMetadata;
import org.tcelor.yahoo.model.SingleChartMetadata;

public class MappedYahooFinanceToWallet {
   public static ChartMetadata mapToWallet(Float base, List<SingleChartMetadata> mappedYahooFinanceList) {
        ChartMetadata wallet = new ChartMetadata();

        if (mappedYahooFinanceList == null || mappedYahooFinanceList.isEmpty()) {
            return wallet;
        }

        int size = mappedYahooFinanceList.size();

        wallet.timestamp = mappedYahooFinanceList.get(0).timestamp;
        wallet.value = new ArrayList<>(wallet.timestamp.size());
        wallet.value.add(base);
        for (int i = 1; i < wallet.timestamp.size(); i++) {
            wallet.value.add(0.0f);
        }
        for (int i = 1; i < wallet.value.size(); i++) {
            for (int j = 0; j < mappedYahooFinanceList.size(); j++) {
                float stockProportion = wallet.value.get(i - 1) / size;
                float change = (mappedYahooFinanceList.get(j).value.get(i)
                        / mappedYahooFinanceList.get(j).value.get(i - 1) - 1);
                wallet.value.set(i, change * stockProportion + stockProportion + wallet.value.get(i));
            }
        }
        for (int i = 0; i < mappedYahooFinanceList.size(); i++) {
            wallet.dividend += mappedYahooFinanceList.get(i).dividend;
            wallet.roe += mappedYahooFinanceList.get(i).roe;
            wallet.pe += mappedYahooFinanceList.get(i).pe;
        }
        wallet.dividend /= mappedYahooFinanceList.size();
        wallet.roe /= mappedYahooFinanceList.size();
        wallet.pe /= mappedYahooFinanceList.size();
        return wallet;
    }
}
