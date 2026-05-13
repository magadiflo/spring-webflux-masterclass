package dev.magadiflo.app.sec05.controller;

import dev.magadiflo.app.sec05.dto.CustomerRequest;
import dev.magadiflo.app.sec05.dto.CustomerResponse;
import dev.magadiflo.app.sec05.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/customers", version = "1")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Mono<ResponseEntity<Flux<CustomerResponse>>> allCustomers() {
        Flux<CustomerResponse> customerResponseFlux = this.customerService.getAllCustomers()
                .doOnNext(customer -> log.info(customer.toString()));
        return Mono.just(ResponseEntity.ok(customerResponseFlux));
    }

    @GetMapping(path = "/simple-pagination")
    public Mono<ResponseEntity<List<CustomerResponse>>> getSimplePaginationCustomers(
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "5") int pageSize) {
        return this.customerService.getAllCustomers(pageNumber, pageSize)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/advanced-pagination")
    public Mono<ResponseEntity<Page<CustomerResponse>>> getAdvancedPaginationCustomers(
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "5") int pageSize) {
        return this.customerService.getAllCustomers(PageRequest.of(pageNumber, pageSize))
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/{customerId}")
    public Mono<ResponseEntity<CustomerResponse>> getCustomer(@PathVariable Long customerId) {
        return this.customerService.getCustomer(customerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerResponse>> saveCustomer(@RequestBody Mono<CustomerRequest> requestMono) {
        return requestMono
                .flatMap(this.customerService::saveCustomer)
                .map(customerResponse -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(customerResponse)
                );
    }

    @PutMapping(path = "/{customerId}")
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(@PathVariable Long customerId,
                                                                 @RequestBody Mono<CustomerRequest> requestMono) {
        return requestMono
                .flatMap(request -> this.customerService.updateCustomer(customerId, request))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{customerId}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Long customerId) {
        return this.customerService.deleteCustomer(customerId)
                .filter(wasDeleted -> wasDeleted)
                .map(wasDeleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
