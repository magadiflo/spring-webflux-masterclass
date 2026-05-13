package dev.magadiflo.app.sec06.mapper;

import dev.magadiflo.app.sec06.dto.CustomerRequest;
import dev.magadiflo.app.sec06.dto.CustomerResponse;
import dev.magadiflo.app.sec06.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toCustomer(CustomerRequest request) {
        return Customer.builder()
                .name(request.name())
                .email(request.email())
                .build();
    }

    public Customer toCustomerUpdate(Customer customer, CustomerRequest request) {
        customer.setName(request.name());
        customer.setEmail(request.email());
        return customer;
    }

    public CustomerResponse toCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }
}
