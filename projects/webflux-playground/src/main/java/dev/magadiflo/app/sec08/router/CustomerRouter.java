package dev.magadiflo.app.sec08.router;

import dev.magadiflo.app.sec08.handler.CustomerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CustomerRouter {

    private static final String CUSTOMERS_URI = "/api/v1/customers";
    private static final String CUSTOMER_ID_PATH = "/{customerId}";

    @Bean
    public RouterFunction<ServerResponse> customerRoutes(CustomerHandler handler) {
        return RouterFunctions.route()
                .GET(CUSTOMERS_URI, handler::allCustomers)
                .GET(CUSTOMERS_URI + CUSTOMER_ID_PATH, handler::getCustomer)
                .POST(CUSTOMERS_URI, handler::saveCustomer)
                .PUT(CUSTOMERS_URI + CUSTOMER_ID_PATH, handler::updateCustomer)
                .DELETE(CUSTOMERS_URI + CUSTOMER_ID_PATH, handler::deleteCustomer)
                .build();
    }
}
