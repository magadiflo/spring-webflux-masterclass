package dev.magadiflo.app.sec10.controller;

import dev.magadiflo.app.sec10.dto.ProductRequest;
import dev.magadiflo.app.sec10.dto.ProductResponse;
import dev.magadiflo.app.sec10.dto.UploadResponse;
import dev.magadiflo.app.sec10.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<ResponseEntity<Flux<ProductResponse>>> downloadProducts() {
        return Mono.just(ResponseEntity.ok(this.productService.findAllProducts()));
    }

    @PostMapping(path = "/upload", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<UploadResponse> uploadProducts(@RequestBody Flux<ProductRequest> productRequestFlux) {
        log.info("Iniciando el proceso de upload de products");

        return this.productService.saveProducts(productRequestFlux.doOnNext(productRequest -> log.info("ProductRequest: {}", productRequest)))
                .then(this.productService.countProducts())
                .map(count -> new UploadResponse(UUID.randomUUID(), count));
    }
}
