package dev.magadiflo.app.sec03.repository;

import dev.magadiflo.app.sec03.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByPriceBetween(int from, int to);

    // findBy sin filtros (a secas) actúa como un findAll, pero limitado por la paginación y ordenamiento
    Flux<Product> findBy(Pageable pageable);
}
