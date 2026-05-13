package dev.magadiflo.aggregator.app.dto;

import dev.magadiflo.aggregator.app.enums.Ticker;
import dev.magadiflo.aggregator.app.enums.TradeAction;

public record StockTradeRequest(Ticker ticker,
                                Integer quantity,
                                Integer price,
                                TradeAction tradeAction) {
    public Integer totalPrice() {
        return this.quantity * this.price;
    }
}
