package io.nebula.market.order.infrastructure.adapter.input.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.nebula.market.order.application.dto.request.UpdateOrderStateRequest;
import io.nebula.market.order.application.service.OrderService;
import io.nebula.market.order.domain.event.OrderCanceledEvent;
import io.nebula.market.order.domain.model.Order;
import io.nebula.market.order.domain.model.OrderState;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "refund", groupId = "order")
    public void handleOrderPlacedEvent(OrderCanceledEvent event) {
        log.info("주문 취소 요청 받음: {}", event);

        UpdateOrderStateRequest updateOrderStateRequest = UpdateOrderStateRequest.builder()
                .id(event.orderId())
                .productId(event.productId())
                .state(OrderState.CANCELED)
                .build();

        Order order = orderService.updateOrderState(updateOrderStateRequest);
        log.info("주문 취소 완료: {}", order);
    }
}
