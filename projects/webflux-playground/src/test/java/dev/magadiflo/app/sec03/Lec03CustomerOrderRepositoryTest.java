package dev.magadiflo.app.sec03;

import dev.magadiflo.app.sec03.entity.Product;
import dev.magadiflo.app.sec03.repository.CustomerOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Lec03CustomerOrderRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Test
    void productsOrderedByCustomer() {
        // given
        String name = "mike";

        // when
        Flux<Product> productFlux = this.customerOrderRepository.getProductOrderedByCustomer(name)
                .doOnNext(product -> log.info("{}", product));

        // then
        StepVerifier.create(productFlux)
                .assertNext(product -> {
                    assertThat(product.getId()).isEqualTo(1);
                    assertThat(product.getDescription()).isEqualTo("iphone 20");
                    assertThat(product.getPrice()).isEqualTo(1000);
                })
                .assertNext(product -> {
                    assertThat(product.getId()).isEqualTo(4);
                    assertThat(product.getDescription()).isEqualTo("mac pro");
                    assertThat(product.getPrice()).isEqualTo(3000);
                })
                .verifyComplete();
    }
}
