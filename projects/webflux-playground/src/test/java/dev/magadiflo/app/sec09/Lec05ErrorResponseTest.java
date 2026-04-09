package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Calculator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

@Slf4j
class Lec05ErrorResponseTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient();

    @Test
    void handlingError() {
        this.client.get()
                .uri("/lec05/calculator/{first}/{second}", 10, 20)
                .header("operation", "@") // operación inválida
                .retrieve()
                .bodyToMono(Calculator.class)
                .onErrorReturn(new Calculator(0, 0, null, 0D)) // respuesta por defecto
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void handlingErrorWithSpecificException() {
        this.client.get()
                .uri("/lec05/calculator/{first}/{second}", 10, 20)
                .header("operation", "@")
                .retrieve()
                .bodyToMono(Calculator.class)
                .onErrorReturn(WebClientResponseException.InternalServerError.class, new Calculator(0, 0, null, 0D))
                .onErrorReturn(WebClientResponseException.BadRequest.class, new Calculator(0, 0, null, -1D))
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void handlingErrorWithDoOnError() {
        this.client.get()
                .uri("/lec05/calculator/{first}/{second}", 10, 20)
                .header("operation", "@")
                .retrieve()
                .bodyToMono(Calculator.class)
                .doOnError(WebClientResponseException.class, e -> {
                    log.error("{}", e.getResponseBodyAs(ProblemDetail.class));
                })
                .onErrorReturn(WebClientResponseException.InternalServerError.class, new Calculator(0, 0, null, 0D))
                .onErrorReturn(WebClientResponseException.BadRequest.class, new Calculator(0, 0, null, -1D))
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
