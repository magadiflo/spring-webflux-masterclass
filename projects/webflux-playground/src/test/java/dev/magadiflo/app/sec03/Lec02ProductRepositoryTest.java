package dev.magadiflo.app.sec03;

import dev.magadiflo.app.sec03.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Lec02ProductRepositoryTest extends AbstractTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findProductsBetweenPrice() {
        this.productRepository.findByPriceBetween(750, 1000)// BETWEEN en la BD incluye los extremos del rango
                .doOnNext(product -> log.info("{}", product))
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertThat(product.getId()).isEqualTo(1L);
                    assertThat(product.getDescription()).isEqualTo("iphone 20");
                })
                .expectNextCount(2) // Validamos que hay 3 productos en total (1 validado + 2 más)
                .verifyComplete();
    }

    @Test
    void pageable() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("price").ascending());
        this.productRepository.findBy(pageable)
                .doOnNext(product -> log.info("{}", product))
                .as(StepVerifier::create)
                .assertNext(product -> assertThat(product.getPrice()).isEqualTo(200))
                .assertNext(product -> assertThat(product.getPrice()).isEqualTo(250))
                .assertNext(product -> assertThat(product.getPrice()).isEqualTo(300))
                .verifyComplete();
    }
}
