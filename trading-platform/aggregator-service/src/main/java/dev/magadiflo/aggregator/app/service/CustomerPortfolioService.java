package dev.magadiflo.aggregator.app.service;

import dev.magadiflo.aggregator.app.client.CustomerServiceClient;
import dev.magadiflo.aggregator.app.client.StockServiceClient;
import dev.magadiflo.aggregator.app.dto.CustomerInformation;
import dev.magadiflo.aggregator.app.dto.StockPriceResponse;
import dev.magadiflo.aggregator.app.dto.StockTradeRequest;
import dev.magadiflo.aggregator.app.dto.StockTradeResponse;
import dev.magadiflo.aggregator.app.dto.TradeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerPortfolioService {

    private final CustomerServiceClient customerServiceClient;
    private final StockServiceClient stockServiceClient;

    public Mono<CustomerInformation> getCustomerInformation(Long customerId) {
        return this.customerServiceClient.getCustomerInformation(customerId);
    }

    public Mono<StockTradeResponse> getStockTrade(Long customerId, TradeRequest tradeRequest) {
        return this.stockServiceClient.getStockPrice(tradeRequest.ticker())
                .map(StockPriceResponse::price)
                .map(price -> toStockTradeRequest(tradeRequest, price))
                .flatMap(stockTradeRequest ->
                        this.customerServiceClient.getStockTrade(customerId, stockTradeRequest));

    }

    private static StockTradeRequest toStockTradeRequest(TradeRequest tradeRequest, Integer price) {
        return new StockTradeRequest(
                tradeRequest.ticker(),
                tradeRequest.quantity(),
                price,
                tradeRequest.tradeAction()
        );
    }
}
