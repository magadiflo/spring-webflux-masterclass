package dev.magadiflo.app.sec03;

import dev.magadiflo.app.sec03.entity.Customer;
import dev.magadiflo.app.sec03.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Lec01CustomerRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void findAll() {
        this.repository.findAll()
                .doOnNext(customer -> log.info("{}", customer))
                .as(StepVerifier::create)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void findById() {
        // given
        Long customerId = 2L;

        // when
        Mono<Customer> customerMono = this.repository.findById(customerId);

        // then
        StepVerifier.create(customerMono)
                .assertNext(customer -> {
                    assertThat(customer)
                            .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                            .containsExactly(2L, "mike", "mike@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void findByName() {
        // given
        String name = "jake";

        // when
        Flux<Customer> customerFlux = this.repository.findByName(name)
                .doOnNext(customer -> log.info("{}", customer));

        // then
        StepVerifier.create(customerFlux)
                .assertNext(customer -> {
                    assertThat(customer)
                            .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                            .containsExactly(3L, "jake", "jake@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void findByEmailEndingWith() {
        // given
        String emailEnding = "@gmail.com";

        // when
        Flux<Customer> customerFlux = this.repository.findByEmailEndingWith(emailEnding)
                .doOnNext(customer -> log.info("{}", customer));

        // then
        StepVerifier.create(customerFlux)
                .expectNextCount(2)
                .assertNext(customer -> {
                    assertThat(customer)
                            .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                            .containsExactly(3L, "jake", "jake@gmail.com");
                })
                .verifyComplete();
    }
}
