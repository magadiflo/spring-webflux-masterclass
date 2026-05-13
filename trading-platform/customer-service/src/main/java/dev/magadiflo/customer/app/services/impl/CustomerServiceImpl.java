package dev.magadiflo.customer.app.services.impl;

import dev.magadiflo.customer.app.dto.CustomerInformation;
import dev.magadiflo.customer.app.entity.Customer;
import dev.magadiflo.customer.app.exception.BusinessErrors;
import dev.magadiflo.customer.app.mapper.CustomerMapper;
import dev.magadiflo.customer.app.repository.CustomerRepository;
import dev.magadiflo.customer.app.repository.PortfolioItemRepository;
import dev.magadiflo.customer.app.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Mono<CustomerInformation> getCustomerInformation(Long customerId) {
        return this.customerRepository.findById(customerId)
                .switchIfEmpty(BusinessErrors.customerNotFound(customerId))
                .flatMap(this::buildCustomerInformation);
    }

    private Mono<CustomerInformation> buildCustomerInformation(Customer customer) {
        return this.portfolioItemRepository.findByCustomerId(customer.getId())
                .collectList()
                .map(portfolioItemList ->
                        this.customerMapper.toCustomerInformation(customer, portfolioItemList));
    }
}
