package io.nebula.market.product.application.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ReadProductsResponse(
        Long id,
        String name,
        String image,
        Long price
) {
}