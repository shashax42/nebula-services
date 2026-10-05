package io.nebula.market.account.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateAccountRequest(
        @NotEmpty @Email String email,
        @NotEmpty String name
) {
}
