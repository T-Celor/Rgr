package org.tcelor.yahoo.model;

import org.tcelor.wallet.model.ChartMetadata;

public class SingleChartMetadata extends ChartMetadata {
    public String ticker;
    
    public SingleChartMetadata buildMetadata() {
        super.buildMetadata();
        return this;
    }

}