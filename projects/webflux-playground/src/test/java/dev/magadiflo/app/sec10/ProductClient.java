package dev.magadiflo.app.sec10;

import dev.magadiflo.app.sec10.dto.ProductRequest;
import dev.magadiflo.app.sec10.dto.UploadResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductClient {
    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    public Mono<UploadResponse> uploadProducts(Flux<ProductRequest> productRequestFlux) {
        return this.client.post()
                .uri("/api/v1/products/upload")
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(productRequestFlux, ProductRequest.class)
                .retrieve()
                .bodyToMono(UploadResponse.class);
    }
}
