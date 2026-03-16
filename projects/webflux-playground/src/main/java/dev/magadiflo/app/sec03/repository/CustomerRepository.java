package dev.magadiflo.app.sec03.repository;

import dev.magadiflo.app.sec03.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}
