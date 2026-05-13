package dev.magadiflo.customer.app.services;

import dev.magadiflo.customer.app.dto.CustomerInformation;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<CustomerInformation> getCustomerInformation(Long customerId);
}
