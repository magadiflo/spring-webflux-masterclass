package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class Lec08BearerAuthTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient(builder -> {
        builder.defaultHeaders(httpHeaders -> {
            httpHeaders.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
        });
    });

    @Test
    void basicAuth() {
        this.client.get()
                .uri("/lec08/product/{productId}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
