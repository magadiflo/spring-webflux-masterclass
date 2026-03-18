package dev.magadiflo.app.sec03.repository;

import dev.magadiflo.app.sec03.entity.CustomerOrder;
import dev.magadiflo.app.sec03.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {
    @Query("""
            SELECT p.id, p.description, p.price
            FROM products AS p
                INNER JOIN customer_orders AS co ON(p.id = co.product_id)
                INNER JOIN customers AS c ON(co.customer_id = c.id)
            WHERE c.name = :name
            """)
    Flux<Product> getProductOrderedByCustomer(String name);
}
