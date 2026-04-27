package dev.magadiflo.customer.app.dto;

import dev.magadiflo.customer.app.enums.Ticker;
import dev.magadiflo.customer.app.enums.TradeAction;

public record StockTradeResponse(Long customerId,
                                 Ticker ticker,
                                 Integer quantity,
                                 Integer price,
                                 TradeAction tradeAction,
                                 Integer totalPrice,
                                 Integer balance) {
}
