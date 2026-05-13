package dev.magadiflo.aggregator.app.client;

import dev.magadiflo.aggregator.app.dto.PriceUpdate;
import dev.magadiflo.aggregator.app.dto.StockPriceResponse;
import dev.magadiflo.aggregator.app.enums.Ticker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class StockServiceClient {

    private final WebClient client;

    /**
     * Instancia única del flujo de precios compartido (hot publisher).
     * Se inicializa de forma lazy (primera llamada a priceUpdateStream()) y se reutiliza
     * en todas las llamadas posteriores, garantizando que todos los consumidores compartan
     * la misma conexión HTTP con el stock-service en lugar de crear N conexiones independientes.
     */
    private Flux<PriceUpdate> priceUpdateFlux;

    /**
     * Consulta el precio actual de una acción en el stock-service.
     * <p>
     * Retorna un {@code Mono<StockPriceResponse>} que emite exactamente un elemento (el precio
     * actual) y luego completa. Es un <b>cold publisher</b>: la petición HTTP se ejecuta cada
     * vez que alguien se suscribe, sin estado compartido entre suscriptores.
     *
     * @param ticker Identificador de la acción a consultar.
     * @return {@code Mono<StockPriceResponse>} con el precio actual de la acción.
     */
    public Mono<StockPriceResponse> getStockPrice(Ticker ticker) {
        return this.client
                .get()
                .uri("/stock/{ticker}", ticker)
                .retrieve() // Modo simple de WebClient: delega el manejo de errores HTTP al framework.
                .bodyToMono(StockPriceResponse.class);
    }

    /**
     * Retorna el flujo compartido y continuo de actualizaciones de precios (hot publisher).
     * <p>
     * Implementa el patrón <b>lazy initialization</b>: la primera vez que se invoca, construye
     * el flujo llamando a {@link #getPriceUpdates()} y lo almacena en {@code priceUpdateFlux}.
     * Las llamadas posteriores retornan la misma instancia ya activa, evitando múltiples
     * conexiones HTTP al stock-service.
     * <p>
     * Este método es el que expone el controlador del aggregator-service hacia los clientes
     * finales como {@code text/event-stream} (SSE).
     *
     * @return {@code Flux<PriceUpdate>} flujo compartido de actualizaciones de precios.
     */
    public Flux<PriceUpdate> priceUpdateStream() {
        // Lazy initialization: solo se crea el flujo la primera vez que se solicita.
        // Las llamadas posteriores reutilizan la misma instancia (misma conexión HTTP).
        if (Objects.isNull(this.priceUpdateFlux)) {
            this.priceUpdateFlux = this.getPriceUpdates();
        }
        return this.priceUpdateFlux;
    }

    /**
     * Construye el flujo de precios con resiliencia y comportamiento de hot publisher.
     * <p>
     * Este método debe invocarse <b>una sola vez</b>. El resultado es un {@code Flux} compartido
     * que todos los consumidores suscriben sin generar nuevas conexiones HTTP. La razón es que
     * todos los consumidores deben recibir el mismo flujo de datos en tiempo real, y la petición
     * al stock-service debe establecerse una única vez.
     * <p>
     * <b>¿Por qué es un hot publisher?</b><br>
     * Gracias al operador {@code cache(1)}, el {@code Flux} frío devuelto por
     * {@link #remotePriceUpdateFlux()} se convierte en un hot publisher. Esto significa que:
     * <ul>
     *   <li>Todos los suscriptores comparten la misma ejecución del flujo origen.</li>
     *   <li>La conexión HTTP se establece una sola vez cuando llega el primer suscriptor.</li>
     *   <li>El flujo permanece activo aunque no haya suscriptores, listo para nuevos consumidores.</li>
     *   <li>Un nuevo suscriptor recibe inmediatamente el último precio emitido sin esperar al próximo evento.</li>
     * </ul>
     *
     * @return {@code Flux<PriceUpdate>} hot publisher con resiliencia ante fallos de conexión.
     */
    private Flux<PriceUpdate> getPriceUpdates() {
        return this.remotePriceUpdateFlux()

                // retryWhen: solo se activa cuando el flujo emite una señal de error (onError).
                // En lugar de propagar el error al suscriptor, reintenta la suscripción al origen
                // (remotePriceUpdateFlux) siguiendo la estrategia definida en retry().
                // Es fundamental para streams de larga duración donde una caída de red no debe
                // terminar el flujo permanentemente.
                .retryWhen(this.retry())

                // cache(1) es equivalente a replay(history=1).autoConnect().
                //
                // replay(1): convierte el Flux frío en un hot publisher que almacena en caché
                // los últimos N elementos emitidos (en nuestro caso N=1 parámetro del replay).
                // Todos los suscriptores comparten la misma ejecución y un nuevo suscriptor
                // recibe el último valor emitido sin volver a ejecutar la llamada HTTP.
                //
                // autoConnect() → autoConnect(1): la conexión al origen se establece cuando
                // llega el primer suscriptor. Una vez iniciado, el flujo continúa activo
                // aunque no haya suscriptores (a diferencia de refCount() que detiene el flujo
                // cuando el último suscriptor se va).
                //
                // ¿Por qué cache(1) y no otras alternativas?
                // - share()   → hot publisher sin caché, nuevos suscriptores no reciben ningún valor previo.
                // - cache()   → almacena TODOS los elementos: en un stream infinito causaría OutOfMemoryError.
                // - cache(1)  → almacena solo el ÚLTIMO elemento, perfecto para streams de precios donde
                //               solo importa el valor más reciente.
                .cache(1);
    }

    /**
     * Establece la conexión HTTP con el endpoint de streaming del stock-service.
     * <p>
     * Consume el endpoint {@code GET /stock/price-stream} que emite una actualización
     * cada vez que el precio de alguna acción cambia.
     * <p>
     * <b>¿Por qué {@code application/x-ndjson} y no {@code text/event-stream}?</b><br>
     * <ul>
     *   <li>{@code text/event-stream} (SSE) → comunicación servidor → cliente final (browser, Postman, curl).</li>
     *   <li>{@code application/x-ndjson} → comunicación backend → backend (servicio a servicio).
     *       Cada línea del stream es un objeto JSON independiente separado por {@code \n}.</li>
     * </ul>
     * Como el aggregator-service es quien consume al stock-service (backend a backend), el tipo
     * correcto es {@code application/x-ndjson}. Luego, el aggregator-service expondrá este stream
     * a los clientes finales como {@code text/event-stream} desde su propio controlador.
     *
     * @return {@code Flux<PriceUpdate>} flujo frío de actualizaciones de precios (una por cambio de precio).
     */
    private Flux<PriceUpdate> remotePriceUpdateFlux() {
        return this.client
                .get()
                .uri("/stock/price-stream")

                // Indica al stock-service que el aggregator-service acepta respuestas en formato NDJSON.
                // Cada línea del stream es un JSON independiente deserializado como un PriceUpdate.
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(PriceUpdate.class);
    }

    /**
     * Define la estrategia de reintento ante fallos en el stream de precios.
     * <p>
     * Utiliza {@code Retry.fixedDelay} que aplica siempre el mismo tiempo de espera entre
     * intentos. Una alternativa más sofisticada sería {@code Retry.backoff} (exponential backoff),
     * que incrementa progresivamente el tiempo de espera para no saturar el servicio remoto en
     * caídas prolongadas. Para un stream de precios donde la reconexión rápida es prioritaria,
     * {@code fixedDelay} es una elección razonable.
     *
     * @return {@code Retry} estrategia de reintento con pausa fija de 1 segundo hasta 100 intentos.
     */
    private Retry retry() {
        return Retry.fixedDelay(100, Duration.ofSeconds(1))   // Hasta 100 reintentos con pausa fija de 1 segundo entre cada uno.
                .doBeforeRetry(retrySignal -> {                     // Acción ejecutada antes de cada reintento.
                    // Registra el motivo del fallo antes de reintentar, útil para diagnóstico y alertas.
                    log.error("Falló la llamada a [http://localhost:7070/stock/price-stream]. Reintentando: {}",
                            retrySignal.failure().getMessage());
                });
    }
}
