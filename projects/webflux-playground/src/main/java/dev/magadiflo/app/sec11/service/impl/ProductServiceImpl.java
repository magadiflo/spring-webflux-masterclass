package dev.magadiflo.app.sec11.service.impl;

import dev.magadiflo.app.sec11.dto.ProductRequest;
import dev.magadiflo.app.sec11.dto.ProductResponse;
import dev.magadiflo.app.sec11.mapper.ProductMapper;
import dev.magadiflo.app.sec11.repository.ProductRepository;
import dev.magadiflo.app.sec11.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final Sinks.Many<ProductResponse> productSink;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Mono<ProductResponse> saveProduct(ProductRequest productRequest) {
        return Mono.just(productRequest)
                .map(this.productMapper::toProduct)
                .flatMap(this.productRepository::save)
                .map(this.productMapper::toProductResponse)
                .doOnNext(this.productSink::tryEmitNext);
    }

    @Override
    public Flux<ProductResponse> getProductStream() {
        return this.productSink.asFlux();
    }
}
