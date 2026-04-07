package dev.magadiflo.app.sec08;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec08")
class CalculatorAssignmentTest {

    @Autowired
    private WebTestClient client;

    @Test
    void calculator() {
        // success
        this.validate(20, 10, "+", 200, "30");
        this.validate(20, 10, "-", 200, "10");
        this.validate(20, 10, "*", 200, "200");
        this.validate(20, 10, "/", 200, "2");

        // bad request
        this.validate(20, 0, "+", 400, "b cannot be 0");
        this.validate(20, 10, "@", 400, "operation header should be + - * /");
        this.validate(20, 10, null, 400, "operation header should be + - * /");
    }

    private void validate(int a, int b, String operation, int status, String expectedResutlt) {
        this.client.get()
                .uri("/api/v1/calculator/{a}/{b}", a, b)
                .headers(httpHeaders -> {
                    if (Objects.nonNull(operation)) {
                        httpHeaders.add("operation", operation);
                    }
                })
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectBody(String.class)
                .value(value -> {
                    assertThat(value)
                            .isNotNull()
                            .isEqualTo(expectedResutlt);
                });
    }
}
