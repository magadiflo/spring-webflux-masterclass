package dev.magadiflo.app.sec08.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CustomerRouter {
    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return null;
    }
}
