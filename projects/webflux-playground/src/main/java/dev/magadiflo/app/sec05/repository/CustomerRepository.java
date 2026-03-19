package dev.magadiflo.app.sec05.repository;

import dev.magadiflo.app.sec05.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}
