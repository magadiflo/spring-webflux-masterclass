package dev.magadiflo.app.sec05;

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
    private WebTestClient cliente;

    @Test
    void allCustomers() {
        // given & when
        WebTestClient.ResponseSpec response = this.cliente.get()
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
        WebTestClient.ResponseSpec response = this.cliente.get()
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
        WebTestClient.ResponseSpec response = this.cliente.get()
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
}
