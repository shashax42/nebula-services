package io.nebula.market.account.application.port.input;

import io.nebula.market.account.application.dto.request.CreateAccountRequest;
import io.nebula.market.account.application.dto.request.DeleteAccountRequest;
import io.nebula.market.account.application.dto.request.UpdateAccountRequest;
import io.nebula.market.account.domain.model.Account;

public interface AccountCommand {
    Account create(CreateAccountRequest request);

    Account update(UpdateAccountRequest request);

    boolean delete(DeleteAccountRequest request);
}
