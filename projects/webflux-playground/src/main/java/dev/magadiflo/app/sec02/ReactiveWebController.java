package dev.magadiflo.app.sec02;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(path = "/api/{version}/reactive", version = "1")
public class ReactiveWebController {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:7070")
            .build();

    @GetMapping(path = "/products")
    public Mono<ResponseEntity<Flux<Product>>> getProducts() {
        Flux<Product> productFlux = this.webClient.get()
                .uri("/demo01/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(product -> log.info("recibido: {}", product));

        return Mono.just(ResponseEntity.ok(productFlux));
    }

    @GetMapping(path = "/products/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<Flux<Product>>> getProductsStream() {
        Flux<Product> productFlux = this.webClient.get()
                .uri("/demo01/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(product -> log.info("recibido stream: {}", product));

        return Mono.just(ResponseEntity.ok(productFlux));
    }
}
