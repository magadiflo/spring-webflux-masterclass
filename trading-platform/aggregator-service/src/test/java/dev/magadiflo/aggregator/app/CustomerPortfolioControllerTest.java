package dev.magadiflo.aggregator.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@Slf4j
class CustomerPortfolioControllerTest extends BaseTest {

    @Test
    void customerInformation() {
        // given
        this.mockCustomerInformation("customer-service/customer-information-200.json", 200);

        // when-then
        this.getCustomerInformation(HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("sam")
                .jsonPath("$.balance").isEqualTo(10000)
                .jsonPath("$.holdings").isArray()
                .jsonPath("$.holdings").isNotEmpty();
    }

    @Test
    void customerNotFound() {
        // given
        this.mockCustomerInformation("customer-service/customer-information-404.json", 404);

        // when-then
        // La respuesta final se construye en el @RestControllerAdvice ApplicationExceptionHandler
        this.getCustomerInformation(HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("El cliente con id [1] no fue encontrado en el customer-service")
                .jsonPath("$.title").isEqualTo("Cliente no encontrado")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.instance").isEqualTo("/api/v1/customers/1");
    }

    private void mockCustomerInformation(String path, int responseCode) {
        String responseBody = this.resourceToString(path);
        this.mockServerClient
                .when(HttpRequest.request("/api/v1/customers/1")) // <--- La petición que hace internamente mi aggregator-service hacia el customer-service
                .respond(HttpResponse.response(responseBody)
                        .withStatusCode(responseCode)
                        .withContentType(MediaType.APPLICATION_JSON));
    }

    private WebTestClient.BodyContentSpec getCustomerInformation(HttpStatus expectedStatus) {
        return this.client
                .get()
                .uri("/api/v1/customers/1") //<-- Este sí es el endpoint de mi controlador del aggregator-service
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))));
    }
}
