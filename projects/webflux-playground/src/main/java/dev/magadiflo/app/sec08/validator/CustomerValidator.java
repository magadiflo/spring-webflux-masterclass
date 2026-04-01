package dev.magadiflo.app.sec08.validator;

import dev.magadiflo.app.sec08.dto.CustomerRequest;
import dev.magadiflo.app.sec08.exception.CustomerErrors;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@UtilityClass
public class CustomerValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static UnaryOperator<Mono<CustomerRequest>> validate() {
        return customerRequestMono -> customerRequestMono
                .filter(hasName())
                .switchIfEmpty(CustomerErrors.missingName())
                .filter(hasValidEmail())
                .switchIfEmpty(CustomerErrors.missingValidEmail());
    }

    private static Predicate<CustomerRequest> hasName() {
        return customerRequest -> hasText(customerRequest.name());
    }

    private static Predicate<CustomerRequest> hasValidEmail() {
        return customerRequest -> hasText(customerRequest.email())
                                  && EMAIL_PATTERN.matcher(customerRequest.email()).matches();
    }

    private static boolean hasText(String value) {
        return Objects.nonNull(value) && !value.isBlank();
    }
}
