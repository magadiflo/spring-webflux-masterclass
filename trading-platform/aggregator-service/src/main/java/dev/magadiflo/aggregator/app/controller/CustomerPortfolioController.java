package dev.magadiflo.aggregator.app.controller;

import dev.magadiflo.aggregator.app.dto.CustomerInformation;
import dev.magadiflo.aggregator.app.dto.StockTradeResponse;
import dev.magadiflo.aggregator.app.dto.TradeRequest;
import dev.magadiflo.aggregator.app.service.CustomerPortfolioService;
import dev.magadiflo.aggregator.app.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/customers", version = "1")
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;

    @GetMapping(path = "/{customerId}")
    public Mono<ResponseEntity<CustomerInformation>> getCustomerInformation(@PathVariable Long customerId) {
        return this.customerPortfolioService.getCustomerInformation(customerId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = "/{customerId}/trade")
    public Mono<ResponseEntity<StockTradeResponse>> trade(@PathVariable Long customerId,
                                                          @RequestBody Mono<TradeRequest> tradeRequestMono) {
        return tradeRequestMono
                .transform(RequestValidator.validate())
                .flatMap(tradeRequest ->
                        this.customerPortfolioService.getStockTrade(customerId, tradeRequest))
                .map(stockTradeResponse ->
                        ResponseEntity.status(HttpStatus.CREATED).body(stockTradeResponse));
    }
}
