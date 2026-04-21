package dev.magadiflo.app.sec11.controller;

import dev.magadiflo.app.sec11.dto.ProductRequest;
import dev.magadiflo.app.sec11.dto.ProductResponse;
import dev.magadiflo.app.sec11.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<Flux<ProductResponse>>> productStream() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(this.productService.getProductStream()));
    }

    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> saveProduct(@RequestBody Mono<ProductRequest> productRequestMono) {
        return productRequestMono
                .flatMap(this.productService::saveProduct)
                .map(productResponse -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(productResponse)
                );
    }
}
