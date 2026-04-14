package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Calculator;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.Map;

class Lec06QueryParamsTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient();

    @Test
    void uriBuilderVariables() {
        String path = "/lec06/calculator";
        String query = "first={first}&second={second}&operation={operation}";
        this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .query(query)
                        .build(10, 20, "+")
                )
                .retrieve()
                .bodyToMono(Calculator.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void uriBuilderMap() {
        String path = "/lec06/calculator";
        String query = "first={first}&second={second}&operation={operation}";
        Map<String, Object> uriVariables = Map.of(
                "first", 30,
                "second", 10,
                "operation", "+"
        );
        this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .query(query)
                        .build(uriVariables)
                )
                .retrieve()
                .bodyToMono(Calculator.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void uriBuilderQueryParam() {
        String path = "/lec06/calculator";
        this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("first", 150)               // valor numérico directo
                        .queryParam("second", 30)               // valor numérico directo
                        .queryParam("operation", "{operation}") // placeholder para carácter especial
                        .build("+")                                // el "+" se codifica como %2B automáticamente
                )
                .retrieve()
                .bodyToMono(Calculator.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void uriBuilderQueryParamPlaceholder() {
        String path = "/lec06/calculator";
        this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("first", "{first}")
                        .queryParam("second", "{second}")
                        .queryParam("operation", "{operation}")
                        .build(80, 60, "+") // se codifica el "+" como %2B
                )
                .retrieve()
                .bodyToMono(Calculator.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
