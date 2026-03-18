package dev.magadiflo.app.sec03.repository;

import dev.magadiflo.app.sec03.entity.CustomerOrder;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {
}
