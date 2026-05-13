package dev.magadiflo.app.sec07;

import dev.magadiflo.app.sec07.dto.CustomerRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec07") //webEnvironment = SpringBootTest.WebEnvironment.MOCK. A partir de ahora lo omitimos, porque es el valor por defecto
class CustomerControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void unauthorized() {
        // No token
        this.client.get()
                .uri("/api/v1/customers")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.title").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.detail").isEqualTo("Error al autenticar");

        // Invalid Token
        this.validateGet("badtoken123", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void standardCategory() {
        this.validateGet("secret123", HttpStatus.OK);
        this.validatePost("secret123", HttpStatus.FORBIDDEN);
    }

    @Test
    void primeCategory() {
        this.validateGet("secret456", HttpStatus.OK);
        this.validatePost("secret456", HttpStatus.CREATED);
    }

    private void validateGet(String token, HttpStatus expectedStatus) {
        this.client.get()
                .uri("/api/v1/customers")
                .header("auth-token", token)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void validatePost(String token, HttpStatus expectedStatus) {
        this.client.post()
                .uri("/api/v1/customers")
                .header("auth-token", token)
                .bodyValue(new CustomerRequest("Lesly", "lesly@gmail.com"))
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}
