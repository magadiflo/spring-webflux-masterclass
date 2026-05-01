package dev.magadiflo.aggregator.app.advice;

import dev.magadiflo.aggregator.app.exception.InvalidTradeRequestException;
import dev.magadiflo.aggregator.app.exception.RemoteClientException;
import dev.magadiflo.aggregator.app.exception.RemoteCustomerNotFoundException;
import dev.magadiflo.aggregator.app.exception.RemoteServerException;
import dev.magadiflo.aggregator.app.exception.RemoteServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    /**
     * El cliente solicitó un recurso que no existe en el servicio remoto.
     * Se propaga como 404 al cliente final porque es un error de negocio claro.
     */
    @ExceptionHandler(RemoteCustomerNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleRemoteCustomerNotFoundException(RemoteCustomerNotFoundException ex) {
        log.error("RemoteCustomerNotFoundException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.NOT_FOUND, ex,
                problemDetail -> problemDetail.setTitle("Cliente no encontrado"));
        return this.getResponseEntityMono(problemDetailResponse);
    }

    /**
     * El servicio remoto rechazó la solicitud (4xx distinto al 404).
     * Se mapea como 502 Bad Gateway porque el aggregator recibió una respuesta
     * inválida de un servicio aguas abajo, no fue un error del cliente final.
     */
    @ExceptionHandler(RemoteClientException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleRemoteClientException(RemoteClientException ex) {
        log.error("RemoteClientException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.BAD_GATEWAY, ex,
                problemDetail -> problemDetail.setTitle("Error en servicio remoto"));
        return this.getResponseEntityMono(problemDetailResponse);
    }

    /**
     * El servicio remoto falló internamente (5xx).
     * También se mapea como 502 Bad Gateway: el aggregator actuó correctamente
     * pero el servicio aguas abajo devolvió una respuesta de error.
     * El status original ya fue logueado en el CustomerServiceClient.
     */
    @ExceptionHandler(RemoteServerException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleRemoteServerException(RemoteServerException ex) {
        log.error("RemoteServerException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.BAD_GATEWAY, ex,
                problemDetail -> problemDetail.setTitle("Error interno en servicio remoto"));
        return this.getResponseEntityMono(problemDetailResponse);
    }

    /**
     * No se pudo establecer conexión con el servicio remoto.
     * Se mapea como 503 Service Unavailable porque el servicio requerido
     * no está accesible en este momento (caída, timeout, DNS, etc.).
     */
    @ExceptionHandler(RemoteServiceUnavailableException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleRemoteServiceUnavailableException(RemoteServiceUnavailableException ex) {
        log.error("RemoteServiceUnavailableException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, ex,
                problemDetail -> problemDetail.setTitle("Servicio remoto no disponible"));
        return this.getResponseEntityMono(problemDetailResponse);
    }

    /**
     * La solicitud de trade recibida no es válida.
     * Se mapea como 400 Bad Request porque el error proviene
     * de datos incorrectos enviados por el cliente final.
     */
    @ExceptionHandler(InvalidTradeRequestException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleInvalidTradeRequestException(InvalidTradeRequestException ex) {
        log.error("InvalidTradeRequestException: {}", ex.getMessage());
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.BAD_REQUEST, ex,
                problemDetail -> problemDetail.setTitle("Solicitud de comercio no válida"));
        return this.getResponseEntityMono(problemDetailResponse);
    }

    /**
     * Red de seguridad para cualquier excepción no contemplada.
     * Garantiza que todas las respuestas de error mantengan el formato ProblemDetail.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGenericException(Exception ex) {
        log.error("Excepción no contemplada: {}", ex.getMessage(), ex);
        var problemDetailResponse = this.buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex,
                problemDetail -> problemDetail.setTitle("Error interno del servidor"));
        return this.getResponseEntityMono(problemDetailResponse);
    }

    private Mono<ResponseEntity<ProblemDetail>> getResponseEntityMono(ProblemDetail problemDetailResponse) {
        return Mono.just(ResponseEntity
                .status(problemDetailResponse.getStatus())
                .body(problemDetailResponse));
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, Exception ex, Consumer<ProblemDetail> problemDetailConsumer) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetailConsumer.accept(problemDetail);
        return problemDetail;
    }
}
