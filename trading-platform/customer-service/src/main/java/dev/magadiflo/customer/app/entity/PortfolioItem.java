package dev.magadiflo.customer.app.entity;

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
    private String ticker;
    private Integer quantity;
}
