package dev.magadiflo.app.sec08.assignment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.function.BiFunction;

@Configuration
public class CalculatorAssignment {

    private static final String CALCULATOR_CHILD_PATH = "/{a}/{b}";

    @Bean
    public RouterFunction<ServerResponse> calculatorRoutes() {
        return RouterFunctions.route()
                .path("/api/v1/calculator", this::calculatorChildRoutes)
                .build();
    }

    private RouterFunction<ServerResponse> calculatorChildRoutes() {
        return RouterFunctions.route()
                .GET("/{a}/0", this.badRequest("b cannot be 0"))
                .GET(CALCULATOR_CHILD_PATH, this.isOperation("+"), this.handle(Integer::sum))
                .GET(CALCULATOR_CHILD_PATH, this.isOperation("-"), this.handle((val1, val2) -> val1 - val2))
                .GET(CALCULATOR_CHILD_PATH, this.isOperation("*"), this.handle((val1, val2) -> val1 * val2))
                .GET(CALCULATOR_CHILD_PATH, this.isOperation("/"), this.handle((val1, val2) -> val1 / val2))
                .GET(CALCULATOR_CHILD_PATH, this.badRequest("operation header should be + - * /"))
                .build();
    }

    private HandlerFunction<ServerResponse> handle(BiFunction<Integer, Integer, Integer> function) {
        return request -> {
            int a = Integer.parseInt(request.pathVariable("a"));
            int b = Integer.parseInt(request.pathVariable("b"));
            int result = function.apply(a, b);
            return ServerResponse.ok().bodyValue(result);
        };
    }

    private RequestPredicate isOperation(String operation) {
        return RequestPredicates.headers(headers ->
                operation.equals(headers.firstHeader("operation")));
    }

    private HandlerFunction<ServerResponse> badRequest(String message) {
        return request -> ServerResponse.badRequest().bodyValue(message);
    }
}
