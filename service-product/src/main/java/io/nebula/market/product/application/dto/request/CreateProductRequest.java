package io.nebula.market.product.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import static io.nebula.market.product.application.dto.Constants.HTTP_PROTOCOL;

@Builder(toBuilder = true)
public record CreateProductRequest(
        @NotEmpty String name,
        @NotEmpty @Pattern(regexp = HTTP_PROTOCOL) String image,
        @NotEmpty String description,
        @NotNull @PositiveOrZero Long price,
        @NotNull @Positive Integer stock
) {
}
