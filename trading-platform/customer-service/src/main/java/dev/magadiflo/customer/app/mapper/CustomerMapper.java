package dev.magadiflo.customer.app.mapper;

import dev.magadiflo.customer.app.dto.CustomerInformation;
import dev.magadiflo.customer.app.dto.Holding;
import dev.magadiflo.customer.app.entity.Customer;
import dev.magadiflo.customer.app.entity.PortfolioItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {

    public CustomerInformation toCustomerInformation(Customer customer, List<PortfolioItem> items) {
        return new CustomerInformation(
                customer.getId(),
                customer.getName(),
                customer.getBalance(),
                this.holdings(items)
        );
    }

    private List<Holding> holdings(List<PortfolioItem> portfolioItems) {
        return portfolioItems
                .stream()
                .map(this::toHolding)
                .toList();
    }

    private Holding toHolding(PortfolioItem portfolioItem) {
        return new Holding(portfolioItem.getTicker(), portfolioItem.getQuantity());
    }
}
