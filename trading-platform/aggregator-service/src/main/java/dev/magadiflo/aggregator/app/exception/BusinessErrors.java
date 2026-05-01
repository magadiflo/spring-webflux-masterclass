package dev.magadiflo.aggregator.app.exception;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class BusinessErrors {
    public static <T> Mono<T> customerNotFound(Long customerId) {
        return Mono.error(() -> new CustomerNotFoundException(customerId));
    }

    public static <T> Mono<T> invalidTradeRequest(String message) {
        return Mono.error(() -> new InvalidTradeRequestException(message));
    }

    public static <T> Mono<T> missingTicker() {
        return Mono.error(() -> new InvalidTradeRequestException("El Ticker es requerido"));
    }

    public static <T> Mono<T> missingTradeAction() {
        return Mono.error(() -> new InvalidTradeRequestException("El TradeAction es requerido"));
    }

    public static <T> Mono<T> invalidQuantity() {
        return Mono.error(() -> new InvalidTradeRequestException("La cantidad debe ser > 0"));
    }
}
