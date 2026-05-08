package dev.magadiflo.aggregator.app.validator;

import dev.magadiflo.aggregator.app.dto.TradeRequest;
import dev.magadiflo.aggregator.app.exception.BusinessErrors;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@UtilityClass
public class RequestValidator {

    public static UnaryOperator<Mono<TradeRequest>> validate() {
        return tradeRequestMono -> tradeRequestMono
                .filter(hasTicker())
                .switchIfEmpty(BusinessErrors.missingTicker())
                .filter(hasTradeAction())
                .switchIfEmpty(BusinessErrors.missingTradeAction())
                .filter(isValidQuantity())
                .switchIfEmpty(BusinessErrors.invalidQuantity());
    }

    private static Predicate<TradeRequest> hasTicker() {
        return tradeRequest -> Objects.nonNull(tradeRequest.ticker());
    }

    private static Predicate<TradeRequest> hasTradeAction() {
        return tradeRequest -> Objects.nonNull(tradeRequest.tradeAction());
    }

    private static Predicate<TradeRequest> isValidQuantity() {
        return tradeRequest -> Objects.nonNull(tradeRequest.quantity()) && tradeRequest.quantity() > 0;
    }
}
