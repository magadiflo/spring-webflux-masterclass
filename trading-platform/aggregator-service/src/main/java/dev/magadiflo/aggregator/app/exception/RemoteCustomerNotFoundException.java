package dev.magadiflo.aggregator.app.exception;

public class RemoteCustomerNotFoundException extends RuntimeException {
    private static final String MESSAGE = "El cliente con id [%d] no fue encontrado en el customer-service";

    public RemoteCustomerNotFoundException(Long customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
