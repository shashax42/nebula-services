package io.nebula.market.order.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder(toBuilder = true)
public record ReadOrdersRequest(@NotNull Pageable pageable) {
}
