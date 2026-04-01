package dev.magadiflo.app.sec08.exception;

public class CustomerNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Cliente con id [%d] no se encuentra";

    public CustomerNotFoundException(Long customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
