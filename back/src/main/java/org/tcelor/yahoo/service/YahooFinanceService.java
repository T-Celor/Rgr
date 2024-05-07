package org.tcelor.yahoo.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.tcelor.yahoo.model.YahooFinanceRatioResponse;
import org.tcelor.yahoo.model.YahooFinanceResponse;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "yahoo-finance-api")
public interface YahooFinanceService {
    @GET
    @Path("/v8/finance/chart/{symbol}")
        Uni<YahooFinanceResponse> getStockData(
            @PathParam("symbol") String symbol,
            @QueryParam("interval") String interval,
            @QueryParam("region") String region,
            @QueryParam("period1") Long period1,
            @QueryParam("period2") Long period2
        );

    @GET
    @Path("/v6/finance/options/{symbol}")
        Uni<YahooFinanceRatioResponse> getRatio(
            @PathParam("symbol") String symbol
        );
}
