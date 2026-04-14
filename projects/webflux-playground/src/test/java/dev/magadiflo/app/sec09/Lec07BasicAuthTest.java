package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class Lec07BasicAuthTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient(builder -> {
        builder.defaultHeaders(httpHeaders -> {
            httpHeaders.setBasicAuth("java", "secret");
        });
    });

    @Test
    void basicAuth() {
        this.client.get()
                .uri("/lec07/product/{productId}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
