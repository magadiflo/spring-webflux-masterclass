package dev.magadiflo.app.sec03;

import dev.magadiflo.app.sec03.projection.OrderDetails;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Lec04DatabaseClientTest extends AbstractTest {

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    void orderDetailsByProduct() {
        // given
        String query = """
                SELECT co.order_id,
                        c.name AS customer_name,
                        p.description AS product_name,
                        co.amount,
                        co.order_date
                FROM products AS p
                    INNER JOIN customer_orders AS co ON(p.id = co.product_id)
                    INNER JOIN customers AS c ON(co.customer_id = c.id)
                WHERE p.description = :description
                ORDER BY co.amount DESC
                """;

        // when
        Flux<OrderDetails> orderDetailsFlux = this.databaseClient
                .sql(query)
                .bind("description", "iphone 18")
                .mapProperties(OrderDetails.class) // Mapeo automático a record
                .all()
                .doOnNext(orderDetails -> log.info("{}", orderDetails));

        // then
        StepVerifier.create(orderDetailsFlux)
                .assertNext(orderDetail -> {
                    assertThat(orderDetail.orderId()).isNotNull();
                    assertThat(orderDetail.customerName()).isEqualTo("sam");
                    assertThat(orderDetail.amount()).isEqualTo(850);
                })
                .assertNext(orderDetail -> {
                    assertThat(orderDetail.orderId()).isNotNull();
                    assertThat(orderDetail.customerName()).isEqualTo("jake");
                    assertThat(orderDetail.amount()).isEqualTo(775);
                }).assertNext(orderDetail -> {
                    assertThat(orderDetail.orderId()).isNotNull();
                    assertThat(orderDetail.customerName()).isEqualTo("jake");
                    assertThat(orderDetail.amount()).isEqualTo(750);
                })
                .verifyComplete();
    }
}
