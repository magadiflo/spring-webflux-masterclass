package dev.magadiflo.app.sec06.service.impl;

import dev.magadiflo.app.sec06.dto.CustomerRequest;
import dev.magadiflo.app.sec06.dto.CustomerResponse;
import dev.magadiflo.app.sec06.mapper.CustomerMapper;
import dev.magadiflo.app.sec06.repository.CustomerRepository;
import dev.magadiflo.app.sec06.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Flux<CustomerResponse> getAllCustomers() {
        return this.customerRepository.findAll()
                .map(this.customerMapper::toCustomerResponse);
    }

    @Override
    public Flux<CustomerResponse> getAllCustomers(int pageNumber, int pageSize) {
        return this.customerRepository.findBy(PageRequest.of(pageNumber, pageSize))
                .map(this.customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        return this.customerRepository.findBy(pageable)
                .map(this.customerMapper::toCustomerResponse)
                .collectList()
                .zipWith(
                        this.customerRepository.count(),
                        (customerResponseList, total) -> new PageImpl<>(customerResponseList, pageable, total)
                );
    }

    @Override
    public Mono<CustomerResponse> getCustomer(Long customerId) {
        return this.customerRepository.findById(customerId)
                .map(this.customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> saveCustomer(CustomerRequest customerRequest) {
        return Mono.just(customerRequest)
                .map(this.customerMapper::toCustomer)
                .flatMap(this.customerRepository::save)
                .map(this.customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> updateCustomer(Long customerId, CustomerRequest customerRequest) {
        return this.customerRepository.findById(customerId)
                .map(customer -> this.customerMapper.toCustomerUpdate(customer, customerRequest))
                .flatMap(this.customerRepository::save)
                .map(this.customerMapper::toCustomerResponse);
    }

    @Override
    public Mono<Boolean> deleteCustomer(Long customerId) {
        return this.customerRepository.deleteCustomerById(customerId);
    }
}
