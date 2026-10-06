package io.nebula.market.product.infrastructure.adapter.output.messaging.producer;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.nebula.market.product.domain.event.OrderCanceledEvent;
import io.nebula.market.product.infrastructure.observability.FunnelMetrics;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, OrderCanceledEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;
    private final FunnelMetrics funnelMetrics;

    @EventListener
    public void handleStockNotAvailableEvent(OrderCanceledEvent event) {
        funnelMetrics.record(FunnelMetrics.STOCK_REJECTED, FunnelMetrics.normalizeReason(event.reason()));
        try {
            Objects.requireNonNull(Observation.createNotStarted("refund", this.observationRegistry).observeChecked(() -> {
                CompletableFuture<SendResult<String, OrderCanceledEvent>> future = kafkaTemplate.send("refund",
                        OrderCanceledEvent.builder()
                                .orderId(event.orderId())
                                .accountId(event.accountId())
                                .productId(event.productId())
                                .price(event.price())
                                .quantity(event.quantity())
                                .reason(event.reason())
                                .build()
                );
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            })).get();

            log.info("주문 실패: {}", event);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
}
