package io.nebula.market.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Builder(toBuilder = true)
public record DeleteAccountRequest(@PathVariable @NotNull UUID id) {
}
