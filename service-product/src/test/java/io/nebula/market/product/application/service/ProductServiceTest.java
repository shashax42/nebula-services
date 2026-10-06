package io.nebula.market.product.application.service;

import io.nebula.market.product.application.dto.request.DeleteProductRequest;
import io.nebula.market.product.application.dto.request.UpdateProductRequest;
import io.nebula.market.product.application.dto.request.UpdateProductStockDecreaseRequest;
import io.nebula.market.product.application.port.output.ProductPort;
import io.nebula.market.product.domain.event.Events;
import io.nebula.market.product.domain.event.OrderCanceledEvent;
import io.nebula.market.product.domain.exception.BizException;
import io.nebula.market.product.domain.model.Product;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * ProductService 단위 테스트.
 * 핵심은 재고 차감(updateStock): 주문 이벤트를 받아 재고를 줄이거나,
 * 재고가 부족하면 OrderCanceledEvent로 주문 취소를 알린다 (Saga 보상 트랜잭션).
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductPort port;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    ProductService service;

    @BeforeEach
    void setUp() {
        Events.setPublisher(publisher);
    }

    @AfterEach
    void tearDown() {
        Events.setPublisher(null);
    }

    private static Product product(long id, int stock) {
        return Product.builder().id(id).name("키보드").price(30_000L).stock(stock).build();
    }

    private static UpdateProductStockDecreaseRequest order(long productId, int quantity, UUID accountId) {
        return UpdateProductStockDecreaseRequest.builder()
                .orderId(100L).productId(productId).accountId(accountId).quantity(quantity)
                .build();
    }

    @Nested
    @DisplayName("재고 차감")
    class UpdateStock {

        @Test
        @DisplayName("재고가 충분하면 주문 수량만큼 줄이고 취소 이벤트는 없다")
        void decreasesStock() {
            given(port.get(1L)).willReturn(Optional.of(product(1L, 5)));

            Product result = service.updateStock(order(1L, 3, UUID.randomUUID()));

            assertThat(result.getStock()).isEqualTo(2);
            then(publisher).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("재고와 주문 수량이 같으면 0이 된다")
        void decreasesToZero() {
            given(port.get(1L)).willReturn(Optional.of(product(1L, 3)));

            assertThat(service.updateStock(order(1L, 3, UUID.randomUUID())).getStock()).isZero();
        }

        @Test
        @DisplayName("재고가 부족하면 재고는 그대로 두고 OrderCanceledEvent를 발행한다")
        void cancelsOrderWhenOutOfStock() {
            UUID accountId = UUID.randomUUID();
            given(port.get(1L)).willReturn(Optional.of(product(1L, 2)));

            Product result = service.updateStock(order(1L, 3, accountId));

            assertThat(result.getStock()).isEqualTo(2);
            ArgumentCaptor<OrderCanceledEvent> event = ArgumentCaptor.forClass(OrderCanceledEvent.class);
            then(publisher).should().publishEvent(event.capture());
            assertThat(event.getValue())
                    .isEqualTo(new OrderCanceledEvent(100L, accountId, 1L, 30_000L, 3, "재고 부족>2"));
        }

        @Test
        @DisplayName("없는 상품이면 NoneExists 예외를 던진다")
        void rejectsUnknownProduct() {
            given(port.get(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateStock(order(1L, 1, UUID.randomUUID())))
                    .isInstanceOf(BizException.NoneExists.class)
                    .hasMessage("없는 상품");
        }
    }

    @Test
    @DisplayName("상품 수정은 요청 값으로 덮어쓴다")
    void updatesProduct() {
        given(port.get(1L)).willReturn(Optional.of(product(1L, 5)));

        Product updated = service.update(UpdateProductRequest.builder()
                .id(1L).name("마우스").image("https://img/1.png").description("무선")
                .price(20_000L).stock(7)
                .build());

        assertThat(updated.getName()).isEqualTo("마우스");
        assertThat(updated.getPrice()).isEqualTo(20_000L);
        assertThat(updated.getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("없는 상품은 삭제하지 않는다")
    void rejectsDeletingUnknownProduct() {
        given(port.exists(1L)).willReturn(false);

        assertThatThrownBy(() -> service.delete(new DeleteProductRequest(1L)))
                .isInstanceOf(BizException.NoneExists.class);
        then(port).should().exists(1L);
        then(port).shouldHaveNoMoreInteractions();
    }
}
