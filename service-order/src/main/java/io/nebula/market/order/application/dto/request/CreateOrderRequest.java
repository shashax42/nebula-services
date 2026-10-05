package io.nebula.market.order.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record CreateOrderRequest(
        @NotNull @Positive Long productId,
        @NotNull UUID accountId,
        @NotNull @PositiveOrZero Long price,
        @NotNull @Positive Integer quantity
) {
}
