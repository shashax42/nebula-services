package io.nebula.market.product.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record UpdateProductStockDecreaseRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive Long productId,
        @NotNull UUID accountId,
        @NotNull @Positive Integer quantity
) {
}
