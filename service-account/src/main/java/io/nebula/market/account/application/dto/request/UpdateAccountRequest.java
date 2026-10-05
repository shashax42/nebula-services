package io.nebula.market.account.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record UpdateAccountRequest(
        @NotNull UUID id,
        @NotEmpty String name
) {
}
