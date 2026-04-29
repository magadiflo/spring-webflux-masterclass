package dev.magadiflo.customer.app.controller;

import dev.magadiflo.customer.app.dto.StockTradeRequest;
import dev.magadiflo.customer.app.enums.Ticker;
import dev.magadiflo.customer.app.enums.TradeAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest
class CustomerControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void customerInformation() {
        this.getBodyContentSpecCustomer(1L, HttpStatus.OK)
                .jsonPath("$.name").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10_000)
                .jsonPath("$.holdings").isEmpty();
    }

    @Test
    void buyAndSell() {
        // buy
        StockTradeRequest buyRequest1 = new StockTradeRequest(Ticker.GOOGLE, 5, 100, TradeAction.BUY);
        this.getBodyContentSpecTrade(2L, buyRequest1, HttpStatus.CREATED)
                .jsonPath("$.balance").isEqualTo(9500)
                .jsonPath("$.totalPrice").isEqualTo(500);

        StockTradeRequest buyRequest2 = new StockTradeRequest(Ticker.GOOGLE, 10, 100, TradeAction.BUY);
        this.getBodyContentSpecTrade(2L, buyRequest2, HttpStatus.CREATED)
                .jsonPath("$.balance").isEqualTo(8500)
                .jsonPath("$.totalPrice").isEqualTo(1000);

        // check the holdings
        this.getBodyContentSpecCustomer(2L, HttpStatus.OK)
                .jsonPath("$.holdings").isNotEmpty()
                .jsonPath("$.holdings").isArray()
                .jsonPath("$.holdings.length()").isEqualTo(1)
                .jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
                .jsonPath("$.holdings[0].quantity").isEqualTo(15);

        // sell
        StockTradeRequest sellRequest1 = new StockTradeRequest(Ticker.GOOGLE, 5, 110, TradeAction.SELL);
        this.getBodyContentSpecTrade(2L, sellRequest1, HttpStatus.CREATED)
                .jsonPath("$.balance").isEqualTo(9050)
                .jsonPath("$.totalPrice").isEqualTo(550);

        StockTradeRequest sellRequest2 = new StockTradeRequest(Ticker.GOOGLE, 10, 110, TradeAction.SELL);
        this.getBodyContentSpecTrade(2L, sellRequest2, HttpStatus.CREATED)
                .jsonPath("$.balance").isEqualTo(10150)
                .jsonPath("$.totalPrice").isEqualTo(1100);

        // check the holdings
        this.getBodyContentSpecCustomer(2L, HttpStatus.OK)
                .jsonPath("$.holdings").isNotEmpty()
                .jsonPath("$.holdings").isArray()
                .jsonPath("$.holdings.length()").isEqualTo(1)
                .jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
                .jsonPath("$.holdings[0].quantity").isEqualTo(0);
    }

    @Test
    void customerNotFound() {
        this.getBodyContentSpecCustomer(5L, HttpStatus.NOT_FOUND)
                .jsonPath("$.title").isEqualTo("Cliente no encontrado")
                .jsonPath("$.detail").isEqualTo("Cliente con id [5] no se encuentra");

        // sell
        StockTradeRequest sellRequest1 = new StockTradeRequest(Ticker.GOOGLE, 5, 110, TradeAction.SELL);
        this.getBodyContentSpecTrade(5L, sellRequest1, HttpStatus.NOT_FOUND)
                .jsonPath("$.title").isEqualTo("Cliente no encontrado")
                .jsonPath("$.detail").isEqualTo("Cliente con id [5] no se encuentra");
    }

    @Test
    void insufficientBalance() {
        StockTradeRequest buyRequest = new StockTradeRequest(Ticker.GOOGLE, 500, 100, TradeAction.BUY);
        this.getBodyContentSpecTrade(2L, buyRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.title").isEqualTo("Saldo insuficiente")
                .jsonPath("$.detail").isEqualTo("Cliente con id [2] no tiene fondos suficientes para completar la transacción");
    }

    @Test
    void insufficientShares() {
        StockTradeRequest buyRequest = new StockTradeRequest(Ticker.GOOGLE, 1, 100, TradeAction.SELL);
        this.getBodyContentSpecTrade(2L, buyRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.title").isEqualTo("Acciones insuficientes")
                .jsonPath("$.detail").isEqualTo("Cliente con id [2] no tiene acciones suficientes para completar la transacción");
    }

    private WebTestClient.BodyContentSpec getBodyContentSpecCustomer(Long customerId, HttpStatus expectedStatus) {
        return this.client.get()
                .uri("/api/v1/customers/{customerId}", customerId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(result ->
                        log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))));
    }

    private WebTestClient.BodyContentSpec getBodyContentSpecTrade(Long customerId, StockTradeRequest request, HttpStatus expectedStatus) {
        return this.client.post()
                .uri("/api/v1/customers/{customerId}/trade", customerId)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(result ->
                        log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))));
    }
}
