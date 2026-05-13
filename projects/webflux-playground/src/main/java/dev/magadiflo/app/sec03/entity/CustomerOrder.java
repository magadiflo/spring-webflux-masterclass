package dev.magadiflo.app.sec03.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "customer_orders")
public class CustomerOrder {
    @Id
    private UUID orderId;       // Clave primaria generada automáticamente con GEN_RANDOM_UUID() (schema.sql)
    private Long customerId;    // FK hacia customers.id
    private Long productId;     // FK hacia products.id
    private Integer amount;     // Monto de la orden
    private Instant orderDate;  // Fecha de la orden en UTC, mapeada correctamente a TIMESTAMP WITH TIME ZONE
}
