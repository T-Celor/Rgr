package org.tcelor.yahoo.resource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.yahoo.mapper.YahooFinanceTickerMapper;
import org.tcelor.yahoo.model.SingleChartMetadata;
import org.tcelor.yahoo.service.YahooFinanceService;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/finance/ticker")
@ApplicationScoped
public class YahooFinanceResource {

    @Inject
    @RestClient
    public YahooFinanceService yahooFinanceService;

    @GET
    @Path("/single") // Can be multiple stocks separate by ','
    public Uni<RestResponse<SingleChartMetadata>> getStockData(@QueryParam("ticker") String ticker,
                                                            @QueryParam("accumulating") Boolean accumulating) {
        long ts = System.currentTimeMillis() / 1000;
        return yahooFinanceService.getStockData(ticker, "1mo", "US", 400000000L, ts)
                .ifNoItem().after(Duration.ofSeconds(5)).failWith(new TimeoutException())
                .flatMap(result -> yahooFinanceService.getRatio(ticker)
                        .ifNoItem().after(Duration.ofSeconds(5)).failWith(new TimeoutException())
                        .map(ratio -> RestResponse.status(Response.Status.OK,
                                YahooFinanceTickerMapper.convertToCustomFormat(result, ticker, accumulating, ratio).buildMetadata())))
                .onFailure().recoverWithItem(failure -> {
                    System.out.println(failure.getMessage());
                    return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR);
                });
    }

    @GET
    @Path("/multiple") //Can be multiple stocks separate by ','
    public Uni<RestResponse<List<SingleChartMetadata>>> getStocksData(@QueryParam("tickers") String tickers,
            @QueryParam("accumulating") Boolean accumulating) {
        long ts = System.currentTimeMillis() / 1000;
        List<String> tickerList = Arrays.stream(tickers.split(",")).map(String::trim).collect(Collectors.toList());
        return Multi.createFrom().iterable(tickerList)
                .onItem().transformToUni(symbol -> yahooFinanceService.getStockData(symbol, "1mo", "US", 400000000L, ts)
                        .ifNoItem().after(Duration.ofSeconds(10)).failWith(new TimeoutException())
                        .flatMap(result -> yahooFinanceService.getRatio(symbol)
                        .ifNoItem().after(Duration.ofSeconds(5)).failWith(new TimeoutException())
                        .map(ratio -> 
                                YahooFinanceTickerMapper.convertToCustomFormat(result, symbol, accumulating, ratio).buildMetadata()))
                        .onFailure().transform(failure -> {
                            Log.info(failure.getMessage() + " for ticker : " + symbol);
                            return null; //Lock the error for upper error
                        }))
                .merge().collect().asList()
                .onItem()
                .transform(resultList -> {
                    Integer minimumSize = resultList.stream().map(t -> t.timestamp.size()).min(Integer::compare).get();
                    //Reorganize data to the lower highest timestamps    
                    resultList.forEach(l -> {
                        l.timestamp = l.timestamp.subList(l.timestamp.size() - minimumSize, l.timestamp.size() - 1);
                        l.value = l.value.subList(l.value.size() - minimumSize, l.value.size() - 1);
                    });
                    return RestResponse.status(Response.Status.OK, resultList);
                })
                .onFailure().recoverWithItem(failure -> RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR));
    }
}