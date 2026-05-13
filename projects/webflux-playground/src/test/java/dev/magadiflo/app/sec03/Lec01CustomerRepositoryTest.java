package dev.magadiflo.app.sec03;

import dev.magadiflo.app.sec03.entity.Customer;
import dev.magadiflo.app.sec03.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Lec01CustomerRepositoryTest extends AbstractTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void findAll() {
        this.repository.findAll()
                .doOnNext(customer -> log.info("{}", customer))
                .as(StepVerifier::create)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void findById() {
        // given
        Long customerId = 2L;

        // when
        Mono<Customer> customerMono = this.repository.findById(customerId);

        // then
        StepVerifier.create(customerMono)
                .assertNext(customer -> {
                    assertThat(customer)
                            .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                            .containsExactly(2L, "mike", "mike@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void findByName() {
        // given
        String name = "jake";

        // when
        Flux<Customer> customerFlux = this.repository.findByName(name)
                .doOnNext(customer -> log.info("{}", customer));

        // then
        StepVerifier.create(customerFlux)
                .assertNext(customer -> {
                    assertThat(customer)
                            .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                            .containsExactly(3L, "jake", "jake@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void findByEmailEndingWith() {
        // given
        String emailEnding = "@gmail.com";

        // when
        Flux<Customer> customerFlux = this.repository.findByEmailEndingWith(emailEnding)
                .doOnNext(customer -> log.info("{}", customer));

        // then
        StepVerifier.create(customerFlux)
                .expectNextCount(2)
                .assertNext(customer -> {
                    assertThat(customer)
                            .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                            .containsExactly(3L, "jake", "jake@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void insertAndDeleteCustomer() {
        // given
        Customer customer = Customer.builder()
                .name("Lesly")
                .email("lesly@gmail.com")
                .build();

        // when
        Mono<Customer> customerMono = this.repository.save(customer)
                .doOnNext(savedCustomer -> log.info("{}", savedCustomer));

        // then
        StepVerifier.create(customerMono)
                .assertNext(savedCustomer -> {
                    assertThat(savedCustomer.getId())
                            .isNotNull();
                    assertThat(savedCustomer)
                            .extracting(Customer::getName, Customer::getEmail)
                            .containsExactly("Lesly", "lesly@gmail.com");
                })
                .verifyComplete();

        // Verificamos que el total de registros aumentó a 11
        this.repository.count()
                .as(StepVerifier::create)
                .expectNext(11L)
                .verifyComplete();

        // Eliminamos el registro insertado y verificamos que vuelve a 10
        this.repository.deleteById(11L)
                .then(this.repository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .verifyComplete();
    }

    @Test
    void updateCustomer() {
        // given
        String name = "ethan";

        // when
        Flux<Customer> customerFlux = this.repository.findByName(name)                      // Buscar clientes por nombre
                .map(customer -> {
                    customer.setName("noel");                                               // Mutar el objeto (cambiar nombre)
                    return customer;
                })
                .flatMap(customer -> this.repository.save(customer)) // Persistir cambios en la BD
                .doOnNext(customer -> log.info("{}", customer));            // Loguear para depuración

        // then
        StepVerifier.create(customerFlux)
                .thenConsumeWhile(customer -> {                       // Consume señales onNext adicionales siempre que coincidan con un predicado.
                    assertThat(customer.getName()).isEqualTo("noel");    // Nos apoyamos de AsserJ para verificar
                    return true;                                                        // La condición para continuar consumiendo onNext, como es true, siempre consumirá, solo lo usamos para consumir los elementos
                })
                .verifyComplete();
    }
}
