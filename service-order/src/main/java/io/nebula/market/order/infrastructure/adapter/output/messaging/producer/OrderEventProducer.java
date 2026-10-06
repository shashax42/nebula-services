package io.nebula.market.order.infrastructure.adapter.output.messaging.producer;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.nebula.market.order.domain.event.OrderPlacedEvent;
import io.nebula.market.order.infrastructure.observability.FunnelMetrics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;
    private final FunnelMetrics funnelMetrics;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        // AFTER_COMMIT 시점 = 주문이 DB 에 확정된 시점
        funnelMetrics.record(FunnelMetrics.ORDER_PLACED);
        try {
            Objects.requireNonNull(Observation.createNotStarted("purchase", this.observationRegistry).observeChecked(() -> {
                CompletableFuture<SendResult<String, OrderPlacedEvent>> future = kafkaTemplate.send("purchase",
                        OrderPlacedEvent.builder()
                                .orderId(event.orderId())
                                .accountId(event.accountId())
                                .productId(event.productId())
                                .price(event.price())
                                .quantity(event.quantity())
                                .build()
                );
                return future.handle((result, throwable) -> {
                    // 기존 동작(전송 실패를 예외로 올리지 않음)은 유지하되, 지표는 실제 전송 결과로 기록한다
                    if (throwable == null) {
                        funnelMetrics.record(FunnelMetrics.PURCHASE_PUBLISHED);
                    } else {
                        funnelMetrics.record(FunnelMetrics.PURCHASE_PUBLISH_FAILED, "publish_error");
                        log.error("주문 이벤트 전송 실패: {}", event.orderId(), throwable);
                    }
                    return CompletableFuture.completedFuture(result);
                });
            })).get();

            log.info("주문 접수: {}", event.orderId());
        } catch (InterruptedException | ExecutionException e) {
            funnelMetrics.record(FunnelMetrics.PURCHASE_PUBLISH_FAILED, "publish_error");
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
}

