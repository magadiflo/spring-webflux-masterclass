package dev.magadiflo.customer.app.exception;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class BusinessErrors {
    public static <T> Mono<T> customerNotFound(Long customerId) {
        return Mono.error(() -> new CustomerNotFoundException(customerId));
    }

    public static <T> Mono<T> insufficientBalance(Long customerId) {
        return Mono.error(() -> new InsufficientBalanceException(customerId));
    }

    public static <T> Mono<T> insufficientShares(Long customerId) {
        return Mono.error(() -> new InsufficientSharesException(customerId));
    }
}
