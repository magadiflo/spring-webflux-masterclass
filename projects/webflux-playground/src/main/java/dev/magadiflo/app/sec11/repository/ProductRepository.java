package dev.magadiflo.app.sec11.repository;

import dev.magadiflo.app.sec11.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}
