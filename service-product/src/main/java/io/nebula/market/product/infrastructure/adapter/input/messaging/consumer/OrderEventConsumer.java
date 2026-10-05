package io.nebula.market.product.infrastructure.adapter.input.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.nebula.market.product.application.dto.request.UpdateProductStockDecreaseRequest;
import io.nebula.market.product.application.service.ProductService;
import io.nebula.market.product.domain.event.OrderPlacedEvent;
import io.nebula.market.product.domain.model.Product;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "purchase", groupId = "order")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("주문 처리중: {}", event);

        UpdateProductStockDecreaseRequest updateProductRequest = UpdateProductStockDecreaseRequest.builder()
                .orderId(event.orderId())
                .accountId(event.accountId())
                .productId(event.productId())
                .quantity(event.quantity())
                .build();

        Product product = productService.updateStock(updateProductRequest);
        log.info("처리 완료: {}", product);
    }
}
