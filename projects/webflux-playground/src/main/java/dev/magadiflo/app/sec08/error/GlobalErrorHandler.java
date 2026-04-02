package dev.magadiflo.app.sec08.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@Slf4j
@Order(-2)
@Component
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    // Spring inyecta tod esto automáticamente
    public GlobalErrorHandler(ErrorAttributes errorAttributes,
                              WebProperties webProperties,
                              ApplicationContext applicationContext,
                              ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);

        // necesario para que Spring sepa cómo serializar las respuestas
        super.setMessageWriters(configurer.getWriters());
        super.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route()
                .onError(NumberFormatException.class, this::handleNumberFormatException)
                .onError(Exception.class, this::handleGenericException)
                .build();
    }

    private Mono<ServerResponse> handleNumberFormatException(NumberFormatException ex, ServerRequest request) {
        log.error(ex.getMessage());

        return this.buildResponse(HttpStatus.BAD_REQUEST, ex, request, problemDetail -> {
            problemDetail.setType(URI.create("https://example.com/problems/invalid-number-format"));
            problemDetail.setTitle("Entrada no válida. Se esperaba un valor numérico");
        });
    }

    private Mono<ServerResponse> handleGenericException(Throwable ex, ServerRequest request) {
        log.error("Excepción no controlada en [{}]", request.path(), ex);

        return this.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request, problemDetail -> {
            problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
            problemDetail.setTitle("Internal Server Error");
        });
    }

    private Mono<ServerResponse> buildResponse(HttpStatus status, Throwable ex, ServerRequest request,
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
