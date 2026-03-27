package dev.magadiflo.app.sec07.advice;

import dev.magadiflo.app.sec07.exception.CustomerNotFoundException;
import dev.magadiflo.app.sec07.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    // Forma 1: usando ResponseEntity dentro de Mono
    @ExceptionHandler(CustomerNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        log.error("{}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("https://example.com/problems/customer-not-found"));
        problemDetail.setTitle("Cliente no encontrado");
        return Mono.just(ResponseEntity
                .status(problemDetail.getStatus())
                .body(problemDetail)
        );
    }

    // Forma 2: retornando directamente ProblemDetail
    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalidInputException(InvalidInputException ex) {
        log.error("{}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("https://example.com/problems/invalid-input"));
        problemDetail.setTitle("Entrada inválida");
        return problemDetail;
    }
}
