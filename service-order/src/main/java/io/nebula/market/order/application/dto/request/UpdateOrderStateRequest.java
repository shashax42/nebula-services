package io.nebula.market.order.application.dto.request;

import lombok.Builder;
import io.nebula.market.order.domain.model.OrderState;

@Builder(toBuilder = true)
public record UpdateOrderStateRequest(
        Long id,
        Long productId,
        OrderState state
) {
}
