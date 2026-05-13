package dev.magadiflo.customer.app.exception;

public class InsufficientBalanceException extends RuntimeException {
    private static final String MESSAGE = "Cliente con id [%d] no tiene fondos suficientes para completar la transacción";

    public InsufficientBalanceException(Long customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
