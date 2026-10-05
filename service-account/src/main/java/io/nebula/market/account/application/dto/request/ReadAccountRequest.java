package io.nebula.market.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record ReadAccountRequest(@NotNull UUID id) {
}
