package dev.magadiflo.app.sec06.repository;

import dev.magadiflo.app.sec06.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    @Modifying
    @Query("DELETE FROM customers WHERE id = :customerId")
    Mono<Boolean> deleteCustomerById(Long customerId);

    Flux<Customer> findBy(Pageable pageable);
}
