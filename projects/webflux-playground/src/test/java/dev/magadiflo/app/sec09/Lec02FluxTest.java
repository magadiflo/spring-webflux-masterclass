package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;

class Lec02FluxTest extends AbstractWebClient {

    private final WebClient webClient = this.createWebClient();

    @Test
    void streamingResponse() {
        this.webClient.get()
                .uri("/lec02/product/stream")
                .accept(MediaType.TEXT_EVENT_STREAM) // Opcional
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void streamingResponse2() {
        this.webClient.get()
                .uri("/lec02/product/stream")
                .accept(MediaType.TEXT_EVENT_STREAM) // Opcional
                .retrieve()
                .bodyToFlux(Product.class)
                .take(Duration.ofSeconds(3)) // Cancelamos después de 3 segundos
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
