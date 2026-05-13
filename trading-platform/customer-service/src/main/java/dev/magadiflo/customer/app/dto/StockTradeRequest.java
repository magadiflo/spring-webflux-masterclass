package dev.magadiflo.customer.app.dto;

import dev.magadiflo.customer.app.enums.Ticker;
import dev.magadiflo.customer.app.enums.TradeAction;

public record StockTradeRequest(Ticker ticker,
                                Integer quantity,
                                Integer price,
                                TradeAction tradeAction) {
    public Integer totalPrice() {
        return this.quantity * this.price;
    }
}
