package dev.magadiflo.app.sec05.repository;

import dev.magadiflo.app.sec05.entity.Customer;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    @Modifying
    @Query("DELETE FROM customers WHERE id = :customerId")
    Mono<Boolean> deleteCustomerById(Long customerId);
}
