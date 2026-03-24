package dev.magadiflo.app.sec05;

import dev.magadiflo.app.sec05.dto.CustomerRequest;
import dev.magadiflo.app.sec05.dto.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec05", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CustomerControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void allCustomers() {
        // given & when
        WebTestClient.ResponseSpec response = this.client.get()
                .uri("/api/v1/customers")// No necesitamos proporcionar toda la url completa http://localhost:8080/api/v1/customers, ¿explica por qué? yo creo que porque estamos usando WebEnvironment.MOCK (valor por defecto)
                .exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CustomerResponse.class)
                .value(customerResponseList -> {
                    log.info("{}", customerResponseList);
                    assertThat(customerResponseList).isNotEmpty();
                })
                .hasSize(10);
    }

    @Test
    void getSimplePaginationCustomers() {
        // given & when
        WebTestClient.ResponseSpec response = this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/customers/simple-pagination")
                        .queryParam("page", 2)
                        .queryParam("size", 2)
                        .build()
                )
                .exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(5)
                .jsonPath("$[1].id").isEqualTo(6);
    }

    @Test
    void getAdvancedPaginationCustomers() {
        // given & when
        WebTestClient.ResponseSpec response = this.client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/customers/advanced-pagination")
                        .queryParam("page", 0)
                        .queryParam("size", 3)
                        .build()
                )
                .exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.content.length()").isEqualTo(3)        // Verificamos que la página tenga 3 elementos
                .jsonPath("$.content[0].id").isEqualTo(1)           // Primer cliente esperado
                .jsonPath("$.content[1].id").isEqualTo(2)           // Segundo cliente esperado
                .jsonPath("$.content[2].id").isEqualTo(3)           // Tercer cliente esperado
                .jsonPath("$.totalElements").isEqualTo(10)          // Total de registros en la tabla
                .jsonPath("$.totalPages").isEqualTo(4)              // Total de páginas calculadas
                .jsonPath("$.first").isEqualTo(true)                // Indicador de primera página
                .jsonPath("$.last").isEqualTo(false);               // No es la última página
    }

    @Test
    void customerById() {
        // given
        Long customerId = 1L;

        // when
        WebTestClient.ResponseSpec response = this.client.get()
                .uri("/api/v1/customers/{customerId}", customerId)
                .exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(customerId)
                .jsonPath("$.name").isEqualTo("sam")
                .jsonPath("$.email").isEqualTo("sam@gmail.com");
    }

    @Test
    void customerById2() {
        // given
        Long customerId = 1L;

        // when
        WebTestClient.ResponseSpec response = this.client.get()
                .uri("/api/v1/customers/{customerId}", customerId)
                .exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CustomerResponse.class)
                .consumeWith(result -> {
                    CustomerResponse customerResponse = result.getResponseBody();
                    log.info("{}", customerResponse);
                    assertThat(customerResponse)
                            .isNotNull()
                            .extracting(CustomerResponse::id, CustomerResponse::name, CustomerResponse::email)
                            .containsExactly(customerId, "sam", "sam@gmail.com");
                });
    }

    @Test
    void createAndDeleteCustomer() {
        // given
        CustomerRequest request = new CustomerRequest("Karen", "kasari@gmail.com");

        // when
        WebTestClient.ResponseSpec response = this.client.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)      //<-- Request
                .accept(MediaType.APPLICATION_JSON)  //<-- Response
                .bodyValue(request)
                .exchange();

        // then
        CustomerResponse customerResponse = response.expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CustomerResponse.class)
                .returnResult()
                .getResponseBody();
        log.info("{}", customerResponse);

        assertThat(customerResponse)
                .isNotNull()
                .extracting(CustomerResponse::name, CustomerResponse::email)
                .containsExactly("Karen", "kasari@gmail.com");
        assertThat(customerResponse.id()).isNotNull();

        // cleanup
        this.client.delete()
                .uri("/api/v1/customers/{customerId}", customerResponse.id())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void updateCustomer() {
        // given
        CustomerRequest request = new CustomerRequest("Noel", "noel@gmail.com");
        Long customerId = 10L;

        // when
        WebTestClient.ResponseSpec exchangeCreate = this.client.put()
                .uri("/api/v1/customers/{id}", customerId)
                .bodyValue(request)
                .exchange();

        // then
        exchangeCreate.expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(result -> log.info("{}", new String(Objects.requireNonNull(result.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(customerId)
                .jsonPath("$.name").isEqualTo("Noel")
                .jsonPath("$.email").isEqualTo("noel@gmail.com");
    }
}
