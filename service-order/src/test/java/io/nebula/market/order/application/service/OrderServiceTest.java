package io.nebula.market.order.application.service;

import io.nebula.market.order.application.dto.request.CreateOrderRequest;
import io.nebula.market.order.application.dto.request.DeleteOrderRequest;
import io.nebula.market.order.application.dto.request.ReadOrderRequest;
import io.nebula.market.order.application.dto.request.UpdateOrderStateRequest;
import io.nebula.market.order.application.port.output.OrderPort;
import io.nebula.market.order.domain.event.Events;
import io.nebula.market.order.domain.event.OrderPlacedEvent;
import io.nebula.market.order.domain.exception.BizException;
import io.nebula.market.order.domain.model.Order;
import io.nebula.market.order.domain.model.OrderState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * OrderService 단위 테스트.
 * 포트(OrderPort, ProductService)만 목으로 대체하므로 DB·Kafka·Redis 없이 실행된다.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderPort port;

    @Mock
    ProductService productService;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    OrderService service;

    @BeforeEach
    void setUp() {
        Events.setPublisher(publisher);
    }

    @AfterEach
    void tearDown() {
        Events.setPublisher(null);
    }

    @Nested
    @DisplayName("주문 생성")
    class Create {

        @Test
        @DisplayName("상품이 있으면 PENDING 상태로 저장하고 OrderPlacedEvent를 발행한다")
        void savesPendingOrderAndPublishesEvent() {
            UUID accountId = UUID.randomUUID();
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .productId(10L).accountId(accountId).price(5_000L).quantity(2)
                    .build();
            given(productService.checkProductExists(10L)).willReturn(true);
            given(port.save(any(Order.class)))
                    .willAnswer(inv -> inv.<Order>getArgument(0).toBuilder().id(1L).build());

            Order order = service.create(request);

            assertThat(order.getId()).isEqualTo(1L);
            assertThat(order.getOrderState()).isEqualTo(OrderState.PENDING);

            ArgumentCaptor<OrderPlacedEvent> event = ArgumentCaptor.forClass(OrderPlacedEvent.class);
            then(publisher).should().publishEvent(event.capture());
            assertThat(event.getValue()).isEqualTo(new OrderPlacedEvent(1L, accountId, 10L, 5_000L, 2));
        }

        @Test
        @DisplayName("상품이 없으면 저장도 이벤트 발행도 하지 않는다")
        void rejectsUnknownProduct() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .productId(99L).accountId(UUID.randomUUID()).price(1L).quantity(1)
                    .build();
            given(productService.checkProductExists(99L)).willReturn(false);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(BizException.NoneExists.class);
            then(port).shouldHaveNoInteractions();
            then(publisher).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class UpdateOrderState {

        @Test
        @DisplayName("기존 주문의 상태를 바꾼다")
        void changesState() {
            Order pending = Order.builder().id(1L).orderState(OrderState.PENDING).build();
            given(port.get(1L)).willReturn(Optional.of(pending));

            Order updated = service.updateOrderState(
                    UpdateOrderStateRequest.builder().id(1L).state(OrderState.SHIPPED).build());

            assertThat(updated.getOrderState()).isEqualTo(OrderState.SHIPPED);
        }

        @Test
        @DisplayName("없는 주문이면 NoneExists 예외를 던진다")
        void rejectsUnknownOrder() {
            given(port.get(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateOrderState(
                    UpdateOrderStateRequest.builder().id(1L).state(OrderState.SHIPPED).build()))
                    .isInstanceOf(BizException.NoneExists.class)
                    .hasMessage("없는 주문");
        }
    }

    @Nested
    @DisplayName("주문 삭제")
    class Delete {

        @Test
        @DisplayName("있는 주문만 삭제한다")
        void deletesExistingOrder() {
            given(port.exists(1L)).willReturn(true);

            boolean deleted = service.delete(new DeleteOrderRequest(1L));

            assertThat(deleted).isTrue();
            ArgumentCaptor<Order> order = ArgumentCaptor.forClass(Order.class);
            then(port).should().delete(order.capture());
            assertThat(order.getValue().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("없는 주문이면 삭제하지 않고 예외를 던진다")
        void rejectsUnknownOrder() {
            given(port.exists(1L)).willReturn(false);

            assertThatThrownBy(() -> service.delete(new DeleteOrderRequest(1L)))
                    .isInstanceOf(BizException.NoneExists.class);
            then(port).should().exists(1L);
            then(port).shouldHaveNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("단건 조회는 포트에서 읽어 온다")
    void readsOrder() {
        Order order = Order.builder().id(1L).orderState(OrderState.PENDING).build();
        given(port.get(1L)).willReturn(Optional.of(order));

        assertThat(service.read(new ReadOrderRequest(1L))).isSameAs(order);
    }
}
