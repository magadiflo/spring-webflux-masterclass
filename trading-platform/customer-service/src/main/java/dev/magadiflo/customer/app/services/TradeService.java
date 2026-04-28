package dev.magadiflo.customer.app.services;

import dev.magadiflo.customer.app.dto.StockTradeRequest;
import dev.magadiflo.customer.app.dto.StockTradeResponse;
import reactor.core.publisher.Mono;

public interface TradeService {
    Mono<StockTradeResponse> trade(Long customerId, StockTradeRequest stockTradeRequest);
}
