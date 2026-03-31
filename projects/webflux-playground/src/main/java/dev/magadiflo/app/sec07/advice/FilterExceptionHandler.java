package dev.magadiflo.app.sec07.advice;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilterExceptionHandler {

    private final ServerCodecConfigurer codecConfigurer;
    private ServerResponse.Context context;

    @PostConstruct
    private void init() {
        this.context = new ContextImpl(this.codecConfigurer);
    }

    public Mono<Void> sendProblemDetail(ServerWebExchange exchange, HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        return ServerResponse
                .status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .bodyValue(problemDetail)
                .flatMap(serverResponse -> serverResponse.writeTo(exchange, this.context));
    }

    private record ContextImpl(ServerCodecConfigurer serverCodecConfigurer) implements ServerResponse.Context {

        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return this.serverCodecConfigurer.getWriters();
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return List.of();
        }
    }
}
