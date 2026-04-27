package dev.magadiflo.customer.app.dto;

import dev.magadiflo.customer.app.enums.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity) {
}
