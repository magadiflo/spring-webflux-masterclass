package dev.magadiflo.customer.app.controller;

import dev.magadiflo.customer.app.dto.CustomerInformation;
import dev.magadiflo.customer.app.dto.StockTradeRequest;
import dev.magadiflo.customer.app.dto.StockTradeResponse;
import dev.magadiflo.customer.app.services.CustomerService;
import dev.magadiflo.customer.app.services.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/customers", version = "1")
public class CustomerController {

    private final CustomerService customerService;
    private final TradeService tradeService;

    @GetMapping(path = "/{customerId}")
    public Mono<ResponseEntity<CustomerInformation>> getCustomerInformation(@PathVariable Long customerId) {
        return this.customerService.getCustomerInformation(customerId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = "/{customerId}/trade")
    public Mono<ResponseEntity<StockTradeResponse>> trade(@PathVariable Long customerId,
                                                          @RequestBody Mono<StockTradeRequest> requestMono) {
        return requestMono
                .flatMap(stockTradeRequest ->
                        this.tradeService.trade(customerId, stockTradeRequest))
                .map(stockTradeResponse ->
                        ResponseEntity.status(HttpStatus.CREATED).body(stockTradeResponse));
    }
}
