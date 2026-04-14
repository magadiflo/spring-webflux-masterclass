package dev.magadiflo.app.sec09;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Slf4j
abstract class AbstractWebClient {

    // Method genérico para imprimir cada item recibido en el flujo
    protected <T> Consumer<T> print() {
        return item -> log.info("recibido: {}", item);
    }

    // WebClient con configuración por defecto
    protected WebClient createWebClient() {
        return this.createWebClient(builder -> {
        });
    }

    // WebClient con posibilidad de personalización a través del consumidor
    protected WebClient createWebClient(Consumer<WebClient.Builder> consumer) {
        WebClient.Builder builder = WebClient.builder().baseUrl("http://localhost:7070/demo02");
        consumer.accept(builder);
        return builder.build();
    }
}
