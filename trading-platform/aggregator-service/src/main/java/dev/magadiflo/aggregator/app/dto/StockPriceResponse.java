package dev.magadiflo.aggregator.app.dto;

import dev.magadiflo.aggregator.app.enums.Ticker;

public record StockPriceResponse(Ticker ticker,
                                 Integer price) {
}
