package dev.magadiflo.app.sec02;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/{version}/traditional", version = "1")
public class TraditionalWebController {

    private final RestClient restClient = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory()) // A partir de Spring Boot 3.4.x es recomendable agregar este factory
            .baseUrl("http://localhost:7070")
            .build();

    @GetMapping(path = "/products")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = this.restClient.get()
                .uri("/demo01/products")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        log.info("respuesta recibida: {}", products);
        return ResponseEntity.ok(products);
    }
}
