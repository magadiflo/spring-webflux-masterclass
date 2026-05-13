package dev.magadiflo.customer.app.exception;

public class InsufficientSharesException extends RuntimeException {
    private static final String MESSAGE = "Cliente con id [%d] no tiene acciones suficientes para completar la transacción";

    public InsufficientSharesException(Long customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
