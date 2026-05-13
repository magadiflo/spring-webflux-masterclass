package dev.magadiflo.customer.app.entity;

import dev.magadiflo.customer.app.enums.Ticker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "portfolio_items")
public class PortfolioItem {
    @Id
    private Long id;
    private Long customerId;
    private Ticker ticker;
    private Integer quantity;
}
