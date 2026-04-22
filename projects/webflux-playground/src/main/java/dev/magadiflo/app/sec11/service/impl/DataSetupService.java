package dev.magadiflo.app.sec11.service.impl;

import dev.magadiflo.app.sec11.service.ProductService;
import dev.magadiflo.app.sec11.dto.ProductRequest;
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

    private final ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        Flux.range(1, 1000)
                .delayElements(Duration.ofSeconds(1))
                .map(i -> new ProductRequest("product-" + i, ThreadLocalRandom.current().nextInt(1, 100)))
                .flatMap(this.productService::saveProduct)
                .doOnNext(productResponse -> log.info("product saved: {}", productResponse))
                .subscribe();
    }
}
