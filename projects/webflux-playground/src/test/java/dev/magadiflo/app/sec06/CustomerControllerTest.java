package dev.magadiflo.app.sec06;

import dev.magadiflo.app.sec06.dto.CustomerRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec06", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CustomerControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void customerNotFound() {
        // get
        this.client.get()
                .uri("/api/v1/customers/{customerId}", 11)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Cliente con id [11] no se encuentra");

        // delete
        this.client.delete()
                .uri("/api/v1/customers/{customerId}", 11)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Cliente con id [11] no se encuentra");

        // put
        CustomerRequest request = new CustomerRequest("Noel", "noel@gmail.com");
        this.client.put()
                .uri("/api/v1/customers/{customerId}", 11)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Cliente con id [11] no se encuentra");
    }

    @Test
    void invalidInputTest() {
        // Nombre faltante
        CustomerRequest missingName = new CustomerRequest(" ", "noel@gmail.com");
        this.client.post()
                .uri("/api/v1/customers")
                .bodyValue(missingName)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("El nombre es requerido");

        // Email faltante
        CustomerRequest missingEmail = new CustomerRequest("Lesly", " ");
        this.client.post()
                .uri("/api/v1/customers")
                .bodyValue(missingEmail)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Se requiere un correo electrónico válido");

        // Email inválido
        CustomerRequest invalidEmail = new CustomerRequest("Noel", "noel.com");
        this.client.post()
                .uri("/api/v1/customers")
                .bodyValue(invalidEmail)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Se requiere un correo electrónico válido");
    }
}
