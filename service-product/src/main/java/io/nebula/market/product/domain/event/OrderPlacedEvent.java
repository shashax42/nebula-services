package io.nebula.market.product.domain.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderPlacedEvent(
        Long orderId,
        UUID accountId,
        Long productId,
        Long price,
        Integer quantity
) {
}
