package dev.magadiflo.aggregator.app.client;

import dev.magadiflo.aggregator.app.dto.CustomerInformation;
import dev.magadiflo.aggregator.app.dto.StockTradeRequest;
import dev.magadiflo.aggregator.app.dto.StockTradeResponse;
import dev.magadiflo.aggregator.app.exception.BusinessErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
@RequiredArgsConstructor
public class CustomerServiceClient {

    private final WebClient client;
    private static final String SERVICE_NAME = "customer-service";

    /**
     * Obtiene la información de un cliente desde el customer-service.
     * <p>
     * Se utiliza {@code exchangeToMono} para interceptar la respuesta HTTP y realizar
     * un mapeo manual de los estados de error (4xx, 5xx) hacia excepciones de negocio
     * personalizadas, extrayendo el detalle del error del {@link ProblemDetail} remoto.
     *
     * @param customerId Identificador único del cliente.
     * @return {@code Mono<CustomerInformation>} con el balance y la lista de activos (holdings) del cliente.
     * @throws RemoteCustomerNotFoundException   si el ID no corresponde a ningún cliente (404).
     * @throws RemoteClientException             si la petición es rechazada por el servicio (4xx).
     * @throws RemoteServerException             si el servicio remoto reporta una falla interna (5xx).
     * @throws RemoteServiceUnavailableException si el servicio no es alcanzable por fallos de red.
     */
    public Mono<CustomerInformation> getCustomerInformation(Long customerId) {
        log.info("[{}] Iniciando solicitud de información del cliente con id: {}",
                SERVICE_NAME, customerId);

        return this.client
                .get()
                .uri("/api/v1/customers/{customerId}", customerId)
                .exchangeToMono(handleResponse(customerId, CustomerInformation.class))
                .transform(addOnErrorResume());
    }

    /**
     * Registra una operación de trading (compra o venta) para un cliente específico.
     * <p>
     * Envía una solicitud al customer-service para procesar una transacción de acciones.
     * El servicio valida la disponibilidad de fondos (en caso de compra) o de acciones
     * (en caso de venta) y retorna el detalle de la operación ejecutada junto con el
     * saldo actualizado.
     *
     * @param customerId Identificador del cliente que realiza la operación.
     * @param request    Objeto con los detalles de la orden (Ticker, cantidad, precio y acción).
     * @return {@code Mono<StockTradeResponse>} con el detalle de la ejecución y balance actualizado.
     * @throws RemoteCustomerNotFoundException   si el ID del cliente no existe en el sistema remoto (404).
     * @throws RemoteClientException             si la solicitud es inválida o no cumple reglas de negocio (4xx).
     * @throws RemoteServerException             si ocurre un error inesperado en el procesamiento remoto (5xx).
     * @throws RemoteServiceUnavailableException si hay fallos críticos de comunicación o conectividad.
     */
    public Mono<StockTradeResponse> getStockTrade(Long customerId, StockTradeRequest request) {
        log.info("[{}] Iniciando solicitud de trade para cliente id: {} [{} {}]",
                SERVICE_NAME, customerId, request.tradeAction(), request.ticker());

        return this.client
                .post()
                .uri("/api/v1/customers/{customerId}/trade", customerId)
                .bodyValue(request)
                .exchangeToMono(handleResponse(customerId, StockTradeResponse.class))
                .transform(addOnErrorResume());
    }

    private static <T> Function<ClientResponse, Mono<T>> handleResponse(Long customerId, Class<T> responseType) {
        return clientResponse -> {
            HttpStatusCode status = clientResponse.statusCode();
            String typeName = responseType.getSimpleName();
            String method = clientResponse.request().getMethod().name();

            // 1. Éxito: deserialización directa del cuerpo
            if (status.is2xxSuccessful()) {
                log.info("[Remote: {}][Op: {}][Type: {}] Respuesta exitosa para cliente con ID: {}",
                        SERVICE_NAME, method, typeName, customerId);
                return clientResponse.bodyToMono(responseType);
            }

            // 2. Error de negocio (404): el cliente no existe
            if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) {
                return clientResponse.createException()
                        .flatMap(webClientResponseException -> {
                            String message = extractMessage(webClientResponseException);
                            log.warn("[Remote: {}][Op: {}][Type: {}] Cliente no encontrado con ID: {}. Status original: {} - Detalle: {}",
                                    SERVICE_NAME, method, typeName, customerId, status, message);
                            return BusinessErrors.remoteCustomerNotFound(customerId);
                        });
            }

            // 3. Otros errores de cliente (4xx): extraemos el ProblemDetail para obtener
            // el mensaje real del servicio remoto y lo encapsulamos en RemoteClientException
            if (status.is4xxClientError()) {
                return clientResponse.createException()
                        .flatMap(webClientResponseException -> {
                            String message = extractMessage(webClientResponseException);
                            log.warn("[Remote: {}][Op: {}][Type: {}] Servicio externo respondió con error de cliente. Status original: {} - Detalle: {}",
                                    SERVICE_NAME, method, typeName, status, message);
                            return BusinessErrors.remoteClient(message);
                        });
            }

            // 4. Errores del servidor (5xx): extraemos el ProblemDetail igual que en 4xx
            // pero los clasificamos como RemoteServerException para distinguir la causa
            if (status.is5xxServerError()) {
                return clientResponse.createException()
                        .flatMap(ex -> {
                            String message = extractMessage(ex);
                            log.error("[Remote: {}][Op: {}][Type: {}] Servicio externo respondió con error de servidor. Status original: {} - Detalle: {}",
                                    SERVICE_NAME, method, typeName, status, message);
                            return BusinessErrors.remoteServer(message);
                        });
            }

            // 5. Cualquier otro status inesperado: delegamos al framework
            log.error("[Remote: {}][Op: {}][Type: {}] Servicio externo respondió con un status inesperado. Status original: {}",
                    SERVICE_NAME, method, typeName, status);
            return clientResponse.createError();
        };
    }

    // 6. Fallos de infraestructura: timeout, DNS, conexión rechazada, etc.
    // WebClientRequestException es la excepción de bajo nivel que lanza WebFlux
    // cuando ni siquiera se pudo establecer la conexión con el servicio remoto
    private static <T> UnaryOperator<Mono<T>> addOnErrorResume() {
        return mono -> mono
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("Fallo de conectividad al comunicarse con {}: {}", SERVICE_NAME, ex.getMessage(), ex);
                    return BusinessErrors.remoteServiceUnavailable("No se pudo establecer conexión con el %s: %s".formatted(SERVICE_NAME, ex.getMessage()));
                });
    }

    private static String extractMessage(WebClientResponseException ex) {
        ProblemDetail problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
        return Objects.nonNull(problemDetail) ? problemDetail.getDetail() : ex.getMessage();
    }
}
