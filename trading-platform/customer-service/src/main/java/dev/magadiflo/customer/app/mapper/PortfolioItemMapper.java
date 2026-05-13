package dev.magadiflo.customer.app.mapper;

import dev.magadiflo.customer.app.dto.StockTradeRequest;
import dev.magadiflo.customer.app.dto.StockTradeResponse;
import dev.magadiflo.customer.app.entity.PortfolioItem;
import dev.magadiflo.customer.app.enums.Ticker;
import org.springframework.stereotype.Component;

@Component
public class PortfolioItemMapper {
    public PortfolioItem toPortfolioItem(Long customerId, Ticker ticker, Integer quantity) {
        return PortfolioItem.builder()
                .customerId(customerId)
                .ticker(ticker)
                .quantity(quantity)
                .build();
    }

    public StockTradeResponse toStockTradeResponse(StockTradeRequest request, Long customerId, Integer balance) {
        return new StockTradeResponse(
                customerId,
                request.ticker(),
                request.quantity(),
                request.price(),
                request.tradeAction(),
                request.totalPrice(),
                balance
        );
    }
}
