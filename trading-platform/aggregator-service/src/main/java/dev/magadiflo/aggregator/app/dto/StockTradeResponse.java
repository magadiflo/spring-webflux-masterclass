package dev.magadiflo.aggregator.app.dto;

import dev.magadiflo.aggregator.app.enums.Ticker;
import dev.magadiflo.aggregator.app.enums.TradeAction;

public record StockTradeResponse(Long customerId,
                                 Ticker ticker,
                                 Integer quantity,
                                 Integer price,
                                 TradeAction tradeAction,
                                 Integer totalPrice,
                                 Integer balance) {
}
