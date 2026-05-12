package dev.magadiflo.aggregator.app;

import dev.magadiflo.aggregator.app.dto.PriceUpdate;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class StockPriceStreamControllerTest extends BaseTest {
    @Test
    void priceStream() {
        String responseBody = this.resourceToString("stock-service/stock-price-stream-200.jsonl");
        this.mockServerClient
                .when(HttpRequest.request("/stock/price-stream"))
                .respond(HttpResponse.response(responseBody)
                        .withStatusCode(200)
                        .withContentType(MediaType.parse("application/x-ndjson")));
        this.client
                .get()
                .uri("/api/v1/stock/price-stream")
                .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(PriceUpdate.class) // Deserializa el stream como Flux<PriceUpdate>
                .getResponseBody() // Obtiene el Flux<PriceUpdate>
                .doOnNext(priceUpdate -> log.info("{}", priceUpdate))  // Log de cada elemento recibido
                .as(StepVerifier::create) // Envuelve el Flux en un StepVerifier
                .assertNext(p -> assertThat(p.price()).isEqualTo(53))  // Verifica el 1er elemento
                .assertNext(p -> assertThat(p.price()).isEqualTo(54))  // Verifica el 2do elemento
                .assertNext(p -> assertThat(p.price()).isEqualTo(55))  // Verifica el 3er elemento
                .verifyComplete();
    }
}
