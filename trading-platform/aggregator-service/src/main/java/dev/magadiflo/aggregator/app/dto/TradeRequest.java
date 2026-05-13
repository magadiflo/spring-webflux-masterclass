package dev.magadiflo.aggregator.app.dto;

import dev.magadiflo.aggregator.app.enums.Ticker;
import dev.magadiflo.aggregator.app.enums.TradeAction;

public record TradeRequest(Ticker ticker,
                           Integer quantity,
                           TradeAction tradeAction) {
}
