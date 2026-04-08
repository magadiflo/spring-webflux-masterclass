package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

class Lec01MonoTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient();

    @Test
    void simpleGet() throws InterruptedException {
        this.client.get()
                .uri("/lec01/product/{id}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .subscribe();

        // Permite que la suscripción finalice antes de que termine el test
        Thread.sleep(Duration.ofSeconds(2));
    }
}
