package dev.magadiflo.customer.app.repository;

import dev.magadiflo.customer.app.entity.PortfolioItem;
import dev.magadiflo.customer.app.enums.Ticker;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PortfolioItemRepository extends ReactiveCrudRepository<PortfolioItem, Long> {
    Flux<PortfolioItem> findByCustomerId(Long customerId);

    Mono<PortfolioItem> findByCustomerIdAndTicker(Long customerId, Ticker ticker);
}
