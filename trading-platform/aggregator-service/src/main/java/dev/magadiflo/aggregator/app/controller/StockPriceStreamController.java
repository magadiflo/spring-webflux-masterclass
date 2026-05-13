package dev.magadiflo.aggregator.app.controller;

import dev.magadiflo.aggregator.app.client.StockServiceClient;
import dev.magadiflo.aggregator.app.dto.PriceUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/stock", version = "1")
public class StockPriceStreamController {

    private final StockServiceClient stockServiceClient;

    @GetMapping(path = "/price-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<Flux<PriceUpdate>>> priceUpdateStream() {
        return Mono.just(ResponseEntity.ok(this.stockServiceClient.priceUpdateStream()));
    }
}
