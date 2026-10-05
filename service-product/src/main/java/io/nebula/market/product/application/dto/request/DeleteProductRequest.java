package io.nebula.market.product.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record DeleteProductRequest(@NotNull Long id) {
}
