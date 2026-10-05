package io.nebula.market.product.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder(toBuilder = true)
public record ReadProductsRequest(@NotNull Pageable pageable) {
}
