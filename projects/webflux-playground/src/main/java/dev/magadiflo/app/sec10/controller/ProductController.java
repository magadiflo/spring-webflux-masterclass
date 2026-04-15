package dev.magadiflo.app.sec10.controller;

import dev.magadiflo.app.sec10.dto.ProductRequest;
import dev.magadiflo.app.sec10.dto.UploadResponse;
import dev.magadiflo.app.sec10.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping(path = "/upload", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<UploadResponse> uploadProducts(@RequestBody Flux<ProductRequest> productRequestFlux) {
        log.info("Iniciando el proceso de upload de products");

        return this.productService.saveProducts(productRequestFlux.doOnNext(productRequest -> log.info("ProductRequest: {}", productRequest)))
                .then(this.productService.countProducts())
                .map(count -> new UploadResponse(UUID.randomUUID(), count));
    }
}
