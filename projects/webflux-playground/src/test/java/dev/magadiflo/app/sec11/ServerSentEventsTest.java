package dev.magadiflo.app.sec11;

import dev.magadiflo.app.sec11.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec11")
class ServerSentEventsTest {
    @Autowired
    private WebTestClient client;

    @Test
    void serverSentEvents() {
        this.client.get()
                .uri("/api/v1/products/stream/{maxPrice}", 80)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(ProductResponse.class)
                .getResponseBody()
                .take(3)
                .doOnNext(productResponse -> log.info("productResponse: {}", productResponse))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(productResponses -> {
                    assertThat(productResponses)
                            .isNotEmpty()
                            .hasSize(3)
                            .allSatisfy(productResponse ->
                                    assertThat(productResponse.price()).isLessThanOrEqualTo(80)
                            );
                })
                .verifyComplete();

    }
}
