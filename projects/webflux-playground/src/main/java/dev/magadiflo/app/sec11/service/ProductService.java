package dev.magadiflo.app.sec11.service;

import dev.magadiflo.app.sec11.dto.ProductRequest;
import dev.magadiflo.app.sec11.dto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<ProductResponse> saveProduct(ProductRequest productRequest);

    Flux<ProductResponse> getProductStream();
}
