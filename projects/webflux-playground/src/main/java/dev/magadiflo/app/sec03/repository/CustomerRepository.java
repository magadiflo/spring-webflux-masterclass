package dev.magadiflo.app.sec03.repository;

import dev.magadiflo.app.sec03.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Flux<Customer> findByName(String name);

    Flux<Customer> findByEmailEndingWith(String emailEnding);
}
