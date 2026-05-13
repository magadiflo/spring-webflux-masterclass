package dev.magadiflo.app.sec08.exception;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class CustomerErrors {
    public static <T> Mono<T> customerNotFound(Long customerId) {
        return Mono.error(() -> new CustomerNotFoundException(customerId));
    }

    public static <T> Mono<T> missingName() {
        return Mono.error(() -> new InvalidInputException("El nombre es requerido"));
    }

    public static <T> Mono<T> missingValidEmail() {
        return Mono.error(() -> new InvalidInputException("Se requiere un correo electrónico válido"));
    }
}
