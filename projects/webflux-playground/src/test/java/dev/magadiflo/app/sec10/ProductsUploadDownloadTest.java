package dev.magadiflo.app.sec10;

import dev.magadiflo.app.sec10.dto.ProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
class ProductsUploadDownloadTest {

    private final ProductClient productClient = new ProductClient();

    @Test
    void uploadProducts() {
        Flux<ProductRequest> productRequestFlux = Flux.just(new ProductRequest("iphone", 1000))
                .delayElements(Duration.ofSeconds(10));

        this.productClient.uploadProducts(productRequestFlux)
                .doOnNext(uploadResponse -> log.info("Recibido: {}", uploadResponse))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void uploadProducts2() {
        Flux<ProductRequest> productRequestFlux = Flux.range(1, 10)
                .map(i -> new ProductRequest("product-" + i, i))
                .delayElements(Duration.ofSeconds(2));

        this.productClient.uploadProducts(productRequestFlux)
                .doOnNext(uploadResponse -> log.info("Recibido2: {}", uploadResponse))
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
