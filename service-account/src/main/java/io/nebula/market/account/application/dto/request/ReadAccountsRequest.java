package io.nebula.market.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder(toBuilder = true)
public record ReadAccountsRequest(
        @NotNull Pageable pageable
) {
}
