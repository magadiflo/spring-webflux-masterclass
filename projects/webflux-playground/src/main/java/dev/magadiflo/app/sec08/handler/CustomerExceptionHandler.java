package dev.magadiflo.app.sec08.handler;

import dev.magadiflo.app.sec08.exception.CustomerNotFoundException;
import dev.magadiflo.app.sec08.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@Slf4j
@Component
public class CustomerExceptionHandler {
    public Mono<ServerResponse> handleNotFoundException(CustomerNotFoundException ex, ServerRequest request) {
        log.error(ex.getMessage());

        return this.buildResponse(HttpStatus.NOT_FOUND, ex, request, problemDetail -> {
            problemDetail.setType(URI.create("https://example.com/problems/customer-not-found"));
            problemDetail.setTitle("Cliente no encontrado");
        });
    }

    public Mono<ServerResponse> handleInvalidInputException(InvalidInputException ex, ServerRequest request) {
        log.error(ex.getMessage());

        return this.buildResponse(HttpStatus.BAD_REQUEST, ex, request, problemDetail -> {
            problemDetail.setType(URI.create("https://example.com/problems/invalid-input"));
            problemDetail.setTitle("Entrada no válida");
        });
    }

    private Mono<ServerResponse> buildResponse(HttpStatus status, Exception ex, ServerRequest request,
                                               Consumer<ProblemDetail> problemDetailConsumer) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setInstance(URI.create(request.path()));

        problemDetailConsumer.accept(problemDetail);

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyValue(problemDetail);
    }
}
