package io.nebula.market.account.application.dto.response;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record ReadAccountResponse(
        UUID id,
        String email,
        String name,
        OffsetDateTime createdDate,
        OffsetDateTime modifiedDate
) {
}
