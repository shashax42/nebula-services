package io.nebula.market.order.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record DeleteOrderRequest(
        @NotNull @Positive Long id
) {
}
