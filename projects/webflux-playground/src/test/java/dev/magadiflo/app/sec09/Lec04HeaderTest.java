package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.Map;

class Lec04HeaderTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient(builder -> {
        builder.defaultHeader("caller-id", "order-service");
    });

    @Test
    void defaultHeader() {
        this.client.get()
                .uri("/lec04/product/{id}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void overrideHeader() {
        this.client.get()
                .uri("/lec04/product/{id}", 1)
                .header("caller-id", "new-service")
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void headersWithMap() {
        var values = Map.of(
                "caller-id", "product-value",
                "some-key", "some-value"
        );
        this.client.get()
                .uri("/lec04/product/{id}", 1)
                .headers(httpHeaders -> httpHeaders.setAll(values)) // aplica todos los headers del map
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
