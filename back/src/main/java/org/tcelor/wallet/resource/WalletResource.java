package org.tcelor.wallet.resource;

import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.wallet.mapper.MappedYahooFinanceToWallet;
import org.tcelor.wallet.model.ChartMetadata;
import org.tcelor.yahoo.resource.YahooFinanceResource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/wallet")
public class WalletResource {

    @Inject
    public YahooFinanceResource yahooFinanceResource;

    @GET
    public Uni<RestResponse<ChartMetadata>> simulateWallet(@QueryParam(value = "tickers") String tickers, @QueryParam(value = "base") Float base, @QueryParam("accumulating") Boolean accumulating) {
        return yahooFinanceResource.getStocksData(tickers, accumulating).onItem()
                .transform(i -> {
                    return RestResponse.status(
                            RestResponse.Status.OK,
                            MappedYahooFinanceToWallet.mapToWallet(base , i.getEntity()).buildMetadata()
                        );
                })
                .onFailure().recoverWithItem(failure -> {
                    return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR);
                });
    }
}
