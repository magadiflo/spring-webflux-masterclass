package dev.magadiflo.app.sec08.handler;

import dev.magadiflo.app.sec08.dto.CustomerRequest;
import dev.magadiflo.app.sec08.dto.CustomerResponse;
import dev.magadiflo.app.sec08.service.CustomerService;
import dev.magadiflo.app.sec08.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerHandler {

    private final CustomerService customerService;

    public Mono<ServerResponse> allCustomers(ServerRequest request) {
        return this.customerService.getAllCustomers()
                .doOnNext(customerResponse -> log.info("{}", customerResponse))
                .as(customerResponseFlux ->
                        ServerResponse.ok().body(customerResponseFlux, CustomerResponse.class));
    }

    public Mono<ServerResponse> getSimplePaginationCustomers(ServerRequest request) {
        Integer pageNumber = this.getIntQueryParam(request, "page", 0);
        Integer pageSize = this.getIntQueryParam(request, "size", 5);
        return this.customerService.getAllCustomers(pageNumber, pageSize)
                .collectList()
                .flatMap(customerResponseList ->
                        ServerResponse.ok().bodyValue(customerResponseList));
    }

    public Mono<ServerResponse> getAdvancedPaginationCustomers(ServerRequest request) {
        Integer pageNumber = this.getIntQueryParam(request, "page", 0);
        Integer pageSize = this.getIntQueryParam(request, "size", 5);
        return this.customerService.getAllCustomers(PageRequest.of(pageNumber, pageSize))
                .flatMap(customerResponsePage ->
                        ServerResponse.ok().bodyValue(customerResponsePage));
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request) {
        return this.customerService.getCustomer(this.customerId(request))
                .flatMap(customerResponse ->
                        ServerResponse.ok().bodyValue(customerResponse));
    }

    public Mono<ServerResponse> saveCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerRequest.class)
                .transform(CustomerValidator.validate())
                .flatMap(this.customerService::saveCustomer)
                .flatMap(customerResponse ->
                        ServerResponse.status(HttpStatus.CREATED).bodyValue(customerResponse));
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerRequest.class)
                .transform(CustomerValidator.validate())
                .flatMap(customerRequest ->
                        this.customerService.updateCustomer(this.customerId(request), customerRequest))
                .flatMap(customerResponse ->
                        ServerResponse.ok().bodyValue(customerResponse));
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        return this.customerService.deleteCustomer(this.customerId(request))
                .then(ServerResponse.noContent().build());
    }

    private long customerId(ServerRequest request) {
        return Long.parseLong(request.pathVariable("customerId"));
    }

    private Integer getIntQueryParam(ServerRequest request, String param, int defaultValue) {
        return request.queryParam(param)
                .map(Integer::parseInt)
                .orElse(defaultValue);
    }
}
