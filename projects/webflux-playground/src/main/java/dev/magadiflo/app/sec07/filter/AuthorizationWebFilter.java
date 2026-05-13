package dev.magadiflo.app.sec07.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Order(2)
@Component
public class AuthorizationWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Inicia la ejecución del filtro de autorización");
        Category category = exchange.getAttributeOrDefault("category", Category.STANDARD);
        log.info("Categoría identificada: {}", category);

        return switch (category) {
            case PRIME -> this.prime(exchange, chain);
            case STANDARD -> this.standard(exchange, chain);
        };
    }

    private Mono<Void> prime(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange);
    }

    private Mono<Void> standard(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Intentando ejecutar método http: {}", exchange.getRequest().getMethod());
        boolean isGet = HttpMethod.GET.equals(exchange.getRequest().getMethod());
        return isGet
                ? chain.filter(exchange)
                : Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
    }
}
