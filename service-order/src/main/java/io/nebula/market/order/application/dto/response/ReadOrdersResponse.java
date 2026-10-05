package io.nebula.market.order.application.dto.response;

import lombok.Builder;
import io.nebula.market.order.domain.model.OrderState;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record ReadOrdersResponse(
        Long id,
        Long price,
        Integer quantity,
        OffsetDateTime orderDate,
        OrderState orderState
) {
}
