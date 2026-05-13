package dev.magadiflo.aggregator.app.dto;

import dev.magadiflo.aggregator.app.enums.Ticker;

import java.time.LocalDateTime;

public record PriceUpdate(Ticker ticker,
                          Integer price,
                          LocalDateTime time) {
}
