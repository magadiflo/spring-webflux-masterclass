package dev.magadiflo.aggregator.app.exception;

public class RemoteServiceUnavailableException extends RuntimeException {
    public RemoteServiceUnavailableException(String message) {
        super(message);
    }
}
