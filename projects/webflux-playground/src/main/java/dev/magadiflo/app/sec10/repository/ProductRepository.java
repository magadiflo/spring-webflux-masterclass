package dev.magadiflo.app.sec10.repository;

import dev.magadiflo.app.sec10.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}
