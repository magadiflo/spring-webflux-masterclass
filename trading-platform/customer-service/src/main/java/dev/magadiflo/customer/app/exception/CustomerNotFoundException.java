package dev.magadiflo.customer.app.exception;

public class CustomerNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Cliente con id [%d] no se encuentra";

    public CustomerNotFoundException(Long customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
