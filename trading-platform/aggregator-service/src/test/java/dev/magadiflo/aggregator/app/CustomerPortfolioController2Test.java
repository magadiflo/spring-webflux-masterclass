package dev.magadiflo.aggregator.app;

import dev.magadiflo.aggregator.app.dto.TradeRequest;
import dev.magadiflo.aggregator.app.enums.Ticker;
import dev.magadiflo.aggregator.app.enums.TradeAction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.RegexBody;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;


@Slf4j
class CustomerPortfolioController2Test extends BaseTest {

    @Test
    void tradeSuccess() {
        this.mockCustomerTrade("customer-service/customer-trade-201.json", 201);
        TradeRequest tradeRequest = new TradeRequest(Ticker.GOOGLE, 2, TradeAction.BUY);
        this.postTrade(tradeRequest, HttpStatus.CREATED)
                .jsonPath("$.balance").isEqualTo(9780)
                .jsonPath("$.totalPrice").isEqualTo(220);
    }

    @Test
    void tradeFailure() {
        this.mockCustomerTrade("customer-service/customer-trade-400.json", 400);
        TradeRequest tradeRequest = new TradeRequest(Ticker.GOOGLE, 2, TradeAction.BUY);
        this.postTrade(tradeRequest, HttpStatus.BAD_GATEWAY)
                .jsonPath("$.detail").isEqualTo("El cliente [id=1] no tiene fondos suficientes para completar la transacción")
                .jsonPath("$.title").isEqualTo("Error en servicio remoto")
                .jsonPath("$.status").isEqualTo(502)
                .jsonPath("$.instance").isEqualTo("/api/v1/customers/1/trade");
    }

    @Test
    void inputValidation() {
        TradeRequest missingTicker = new TradeRequest(null, 2, TradeAction.BUY);
        this.postTrade(missingTicker, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("El Ticker es requerido");

        TradeRequest missingAction = new TradeRequest(Ticker.GOOGLE, 2, null);
        this.postTrade(missingAction, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("El TradeAction es requerido");

        TradeRequest invalidQuantity = new TradeRequest(Ticker.GOOGLE, -2, TradeAction.BUY);
        this.postTrade(invalidQuantity, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("La cantidad debe ser > 0");
    }

    private void mockCustomerTrade(String customerServiceFilePath, int responseCode) {
        // 1. Mock: stock-service → GET /stock/GOOGLE
        String stockResponseBody = this.resourceToString("stock-service/stock-price-200.json");
        this.mockServerClient
                .when(HttpRequest.request("/stock/GOOGLE"))
                .respond(HttpResponse.response(stockResponseBody)
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON));

        // 2. Mock: customer-service → POST /api/v1/customers/1/trade
        String customerResponseBody = this.resourceToString(customerServiceFilePath);
        this.mockServerClient
                .when(HttpRequest.request("/api/v1/customers/1/trade")
                        .withMethod("POST")
                        .withBody(RegexBody.regex(".*\"price\":110.*")))
                .respond(HttpResponse.response(customerResponseBody)
                        .withStatusCode(responseCode)
                        .withContentType(MediaType.APPLICATION_JSON));
    }

    private WebTestClient.BodyContentSpec postTrade(TradeRequest tradeRequest, HttpStatus expectedStatus) {
        return this.client
                .post()
                .uri("/api/v1/customers/1/trade")
                .bodyValue(tradeRequest)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(result -> log.info("{}",
                        new String(Objects.requireNonNull(result.getResponseBody()))));
    }
}
