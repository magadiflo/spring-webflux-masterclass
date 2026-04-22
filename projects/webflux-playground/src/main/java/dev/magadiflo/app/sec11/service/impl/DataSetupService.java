package dev.magadiflo.app.sec11.service.impl;

import dev.magadiflo.app.sec11.dto.ProductRequest;
import dev.magadiflo.app.sec11.mapper.ProductMapper;
import dev.magadiflo.app.sec11.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataSetupService implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void run(String... args) throws Exception {
        Flux.range(1, 1000)
                .delayElements(Duration.ofSeconds(1))
                .map(i -> new ProductRequest("product-" + i, ThreadLocalRandom.current().nextInt(1, 100)))
                .map(this.productMapper::toProduct)
                .flatMap(this.productRepository::save)
                .doOnNext(product -> log.info("product saved: {}", product))
                .subscribe();
    }
}
