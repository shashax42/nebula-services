package io.nebula.market.account.application.dto.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record ReadAccountsResponse(
        String email,
        String name,
        OffsetDateTime createdDate,
        OffsetDateTime modifiedDate
) {
}
