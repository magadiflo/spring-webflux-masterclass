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

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Flux<ProductResponse> findAllProducts() {
        return this.productRepository.findAll()
                .map(this.productMapper::toProductResponse);
    }

    @Override
    public Flux<ProductResponse> saveProducts(Flux<ProductRequest> productRequestFlux) {
        return productRequestFlux
                .map(this.productMapper::toProduct)
                .as(this.productRepository::saveAll)
                .map(this.productMapper::toProductResponse);
    }

    @Override
    public Mono<Long> countProducts() {
        return this.productRepository.count();
    }
}
