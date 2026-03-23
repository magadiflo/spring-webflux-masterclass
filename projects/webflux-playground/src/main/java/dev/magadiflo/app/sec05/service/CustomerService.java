package dev.magadiflo.app.sec05.service;

import dev.magadiflo.app.sec05.dto.CustomerRequest;
import dev.magadiflo.app.sec05.dto.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerResponse> getAllCustomers();

    Flux<CustomerResponse> getAllCustomers(int pageNumber, int pageSize);

    Mono<CustomerResponse> getCustomer(Long customerId);

    Mono<CustomerResponse> saveCustomer(CustomerRequest customerRequest);

    Mono<CustomerResponse> updateCustomer(Long customerId, CustomerRequest customerRequest);

    Mono<Boolean> deleteCustomer(Long customerId);
}
