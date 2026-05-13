package dev.magadiflo.customer.app.services.impl;

import dev.magadiflo.customer.app.dto.StockTradeRequest;
import dev.magadiflo.customer.app.dto.StockTradeResponse;
import dev.magadiflo.customer.app.entity.Customer;
import dev.magadiflo.customer.app.entity.PortfolioItem;
import dev.magadiflo.customer.app.exception.BusinessErrors;
import dev.magadiflo.customer.app.mapper.PortfolioItemMapper;
import dev.magadiflo.customer.app.repository.CustomerRepository;
import dev.magadiflo.customer.app.repository.PortfolioItemRepository;
import dev.magadiflo.customer.app.services.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class TradeServiceImpl implements TradeService {

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final PortfolioItemMapper portfolioItemMapper;

    @Override
    @Transactional
    public Mono<StockTradeResponse> trade(Long customerId, StockTradeRequest stockTradeRequest) {
        return switch (stockTradeRequest.tradeAction()) {
            case BUY -> this.buyStock(customerId, stockTradeRequest);
            case SELL -> this.sellStock(customerId, stockTradeRequest);
        };
    }

    private Mono<StockTradeResponse> buyStock(Long customerId, StockTradeRequest stockTradeRequest) {
        Mono<Customer> customerMono = this.customerRepository.findById(customerId)
                .switchIfEmpty(BusinessErrors.customerNotFound(customerId))
                .filter(customer -> customer.getBalance() >= stockTradeRequest.totalPrice())
                .switchIfEmpty(BusinessErrors.insufficientBalance(customerId));

        Mono<PortfolioItem> portfolioItemMono = this.portfolioItemRepository.findByCustomerIdAndTicker(customerId, stockTradeRequest.ticker())
                .defaultIfEmpty(this.portfolioItemMapper.toPortfolioItem(customerId, stockTradeRequest.ticker(), 0));

        //zipWhen te permite ejecutar un segundo Mono basándonos en el valor del primero, y luego combina ambos resultados.
        //Es como una combinación de flatMap + zip.
        //Aquí queremos que primero se ejecute el customerMono, si existe y está bien ese flujo, recién ejecutamos el siguiente portfolioItemMono.
        return customerMono.zipWhen(customer -> portfolioItemMono)
                .flatMap(tuple ->
                        this.executeBuy(tuple.getT1(), tuple.getT2(), stockTradeRequest));
    }

    private Mono<StockTradeResponse> sellStock(Long customerId, StockTradeRequest stockTradeRequest) {
        Mono<Customer> customerMono = this.customerRepository.findById(customerId)
                .switchIfEmpty(BusinessErrors.customerNotFound(customerId));

        Mono<PortfolioItem> portfolioItemMono = this.portfolioItemRepository.findByCustomerIdAndTicker(customerId, stockTradeRequest.ticker())
                .filter(portfolioItem -> portfolioItem.getQuantity() >= stockTradeRequest.quantity())
                .switchIfEmpty(BusinessErrors.insufficientShares(customerId)); // Aquí sí importa que el switchIfEmpty esté al final para asegurarnos de que nunca el portfolioItemMono vaya al zipWhen como un Mono.empty()

        return customerMono.zipWhen(customer -> portfolioItemMono)
                .flatMap(tuple ->
                        this.executeSell(tuple.getT1(), tuple.getT2(), stockTradeRequest));
    }

    private Mono<StockTradeResponse> executeBuy(Customer customer, PortfolioItem portfolioItem, StockTradeRequest stockTradeRequest) {
        customer.setBalance(customer.getBalance() - stockTradeRequest.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() + stockTradeRequest.quantity());
        return this.saveAndBuildResponse(customer, portfolioItem, stockTradeRequest);
    }

    private Mono<StockTradeResponse> executeSell(Customer customer, PortfolioItem portfolioItem, StockTradeRequest stockTradeRequest) {
        customer.setBalance(customer.getBalance() + stockTradeRequest.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() - stockTradeRequest.quantity());
        return this.saveAndBuildResponse(customer, portfolioItem, stockTradeRequest);
    }

    private Mono<StockTradeResponse> saveAndBuildResponse(Customer customer, PortfolioItem portfolioItem, StockTradeRequest stockTradeRequest) {
        StockTradeResponse stockTradeResponse =
                this.portfolioItemMapper.toStockTradeResponse(stockTradeRequest, customer.getId(), customer.getBalance());
        /**
         * (No emite valores Mono<Void>) Mono.when: Agrupe los publishers proporcionados en un nuevo Mono
         * que se completará cuando todas las fuentes especificadas hayan finalizado. Un error provocará la
         * cancelación de los resultados pendientes y la emisión inmediata de un error al Mono resultante.
         */
        return Mono.when(
                this.customerRepository.save(customer),
                this.portfolioItemRepository.save(portfolioItem)
        ).thenReturn(stockTradeResponse);
    }
}
