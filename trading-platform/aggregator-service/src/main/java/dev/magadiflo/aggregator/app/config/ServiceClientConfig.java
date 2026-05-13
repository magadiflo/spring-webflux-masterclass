package dev.magadiflo.aggregator.app.config;

import dev.magadiflo.aggregator.app.client.CustomerServiceClient;
import dev.magadiflo.aggregator.app.client.StockServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class ServiceClientConfig {

    @Bean
    public CustomerServiceClient customerServiceClient(@Value("${external.services.customer.base-url}") String baseUrl) {
        return new CustomerServiceClient(this.createWebClient(baseUrl));
    }

    @Bean
    public StockServiceClient stockServiceClient(@Value("${external.services.stock.base-url}") String baseUrl) {
        return new StockServiceClient(this.createWebClient(baseUrl));
    }

    private WebClient createWebClient(String baseUrl) {
        log.info("baseUrl: {}", baseUrl);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
