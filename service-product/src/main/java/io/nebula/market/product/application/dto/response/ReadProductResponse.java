package io.nebula.market.product.application.dto.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record ReadProductResponse(
        Long id,
        String name,
        String image,
        String description,
        Long price,
        Integer stock,
        OffsetDateTime createdDate,
        OffsetDateTime modifiedDate
) {
}