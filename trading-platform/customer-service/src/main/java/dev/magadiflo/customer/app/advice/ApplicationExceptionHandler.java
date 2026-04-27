package dev.magadiflo.customer.app.advice;

import dev.magadiflo.customer.app.exception.CustomerNotFoundException;
import dev.magadiflo.customer.app.exception.InsufficientBalanceException;
import dev.magadiflo.customer.app.exception.InsufficientSharesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        log.error("CustomerNotFoundException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.NOT_FOUND, ex, problemDetail -> {
            problemDetail.setTitle("Cliente no encontrado");
        });
        return Mono.just(ResponseEntity
                .status(problemDetailResponse.getStatus())
                .body(problemDetailResponse));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.error("InsufficientBalanceException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.BAD_REQUEST, ex, problemDetail -> {
            problemDetail.setTitle("Saldo insuficiente");
        });
        return Mono.just(ResponseEntity
                .status(problemDetailResponse.getStatus())
                .body(problemDetailResponse));
    }

    @ExceptionHandler(InsufficientSharesException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleInsufficientSharesException(InsufficientSharesException ex) {
        log.error("InsufficientSharesException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.BAD_REQUEST, ex, problemDetail -> {
            problemDetail.setTitle("Acciones insuficientes");
        });
        return Mono.just(ResponseEntity
                .status(problemDetailResponse.getStatus())
                .body(problemDetailResponse));
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, Exception ex, Consumer<ProblemDetail> problemDetailConsumer) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetailConsumer.accept(problemDetail);
        return problemDetail;
    }
}
