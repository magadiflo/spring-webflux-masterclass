package dev.magadiflo.customer.app.repository;

import dev.magadiflo.customer.app.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
}
