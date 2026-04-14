package dev.magadiflo.app.sec09;

import dev.magadiflo.app.sec09.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.UUID;

@Slf4j
class Lec09ExchangeFilterTest extends AbstractWebClient {

    private final WebClient client = this.createWebClient(builder ->
            builder.filter(this.tokenGenerator())
                    .filter(this.requestLogger())
    );

    @Test
    void exchangeFilter() {
        this.client.get()
                .uri("/lec09/product/{productId}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(this.print())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    private ExchangeFilterFunction tokenGenerator() {
        return (request, next) -> {
            String token = UUID.randomUUID().toString().replace("-", "");
            log.info("Token generado: {}", token);

            ClientRequest modifiedRequest = ClientRequest.from(request)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .build();
            return next.exchange(modifiedRequest);
        };
    }

    private ExchangeFilterFunction requestLogger() {
        return (request, next) -> {
            log.info("Request url [{}]: {}", request.method(), request.url());
            return next.exchange(request);
        };
    }
}
