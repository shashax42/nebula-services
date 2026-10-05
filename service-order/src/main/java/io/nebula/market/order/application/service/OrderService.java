package io.nebula.market.order.application.service;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import io.nebula.market.order.application.dto.request.*;
import io.nebula.market.order.application.port.input.OrderCommand;
import io.nebula.market.order.application.port.input.OrderQuery;
import io.nebula.market.order.application.port.output.OrderPort;
import io.nebula.market.order.domain.event.Events;
import io.nebula.market.order.domain.event.OrderPlacedEvent;
import io.nebula.market.order.domain.exception.BizException;
import io.nebula.market.order.domain.model.Order;
import io.nebula.market.order.domain.model.OrderState;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService implements OrderCommand, OrderQuery {
    private final OrderPort port;
    private final ProductService productService;

    @Override
    @Transactional
    public Order create(CreateOrderRequest request) {
        if (!productService.checkProductExists(request.productId())) {
            throw new BizException.NoneExists();
        }

        Order order = port.save(Order.builder()
                .accountId(request.accountId())
                .productId(request.productId())
                .price(request.price())
                .quantity(request.quantity())
                .orderState(OrderState.PENDING)
                .build());

        Events.raise(OrderPlacedEvent.builder()
                .orderId(order.getId())
                .accountId(order.getAccountId())
                .productId(order.getProductId())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .build());

        return order;
    }

    @Override
    @Transactional
    public boolean delete(DeleteOrderRequest request) {
        if (!port.exists(request.id())) {
            throw new BizException.NoneExists();
        }

        Order order = Order.builder()
                .id(request.id())
                .build();

        port.delete(order);
        return true;
    }

    @Override
    @Transactional
    public Order updateOrderState(UpdateOrderStateRequest request) {
        Order order = port.get(request.id())
                .orElseThrow(BizException.NoneExists::new);

        return order.changeOrderState(request.state());
    }

    @Override
    @Cacheable(value = "order", key = "#query.id()", cacheManager = "orderCacheManager")
    @Retryable(retryFor = {RedisConnectionException.class}, maxAttempts = 1, backoff = @Backoff(delay = 100))
    public Order read(ReadOrderRequest query) {
        return getOrder(query);
    }

    private Order getOrder(ReadOrderRequest query) {
        return port.get(query.id())
                .orElseThrow(BizException.NoneExists::new);
    }

    @Recover
    @SuppressWarnings("unused")
    public Order readWithoutCache(ReadOrderRequest query) {
        return getOrder(query);
    }

    @Override
    @Cacheable(value = "orders", key = "#query.pageable()", cacheManager = "orderCacheManager")
    @Retryable(retryFor = {RedisConnectionException.class}, maxAttempts = 1, backoff = @Backoff(delay = 100))
    public Page<Order> read(ReadOrdersRequest query) {
        return getOrders(query);
    }

    @Recover
    @SuppressWarnings("unused")
    public Page<Order> readWithoutCache(ReadOrdersRequest query) {
        return getOrders(query);
    }

    private Page<Order> getOrders(ReadOrdersRequest query) {
        return port.getMultiple(query.pageable());
    }
}
