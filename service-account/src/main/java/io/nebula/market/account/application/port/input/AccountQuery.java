package io.nebula.market.account.application.port.input;

import io.nebula.market.account.application.dto.request.ReadAccountRequest;
import io.nebula.market.account.application.dto.request.ReadAccountsRequest;
import io.nebula.market.account.domain.model.Account;
import org.springframework.data.domain.Page;

public interface AccountQuery {
    Account read(ReadAccountRequest query);

    Page<Account> read(ReadAccountsRequest query);
}
