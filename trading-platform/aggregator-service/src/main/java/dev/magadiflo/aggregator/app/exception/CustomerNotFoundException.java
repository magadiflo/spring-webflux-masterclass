package dev.magadiflo.aggregator.app.exception;

public class CustomerNotFoundException extends RuntimeException {
    private static final String MESSAGE = "No se encuentra el cliente con id: %d";

    public CustomerNotFoundException(Long customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
