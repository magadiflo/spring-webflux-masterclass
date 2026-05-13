package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

class Lec03PostTest extends AbstractWebClient {

    private final WebClient webClient = this.createWebClient();

    @Test
    void postBodyValue() {
        Product product = new Product(null, "Monitor", 200);
        this.webClient.post()
                .uri("/lec03/product")
                .bodyValue(product)// El objeto ya está disponible en memoria
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void postBody() {
        Mono<Product> productMono = Mono.fromSupplier(() -> new Product(null, "Monitor", 200))
                .delayElement(Duration.ofSeconds(1)); // Simula retraso en la generación del dato
        this.webClient.post()
                .uri("/lec03/product")
                .body(productMono, Product.class) // Se pasa el publisher
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
