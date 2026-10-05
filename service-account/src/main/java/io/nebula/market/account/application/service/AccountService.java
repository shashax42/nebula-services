package io.nebula.market.account.application.service;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import io.nebula.market.account.application.dto.request.*;
import io.nebula.market.account.application.port.input.AccountCommand;
import io.nebula.market.account.application.port.input.AccountQuery;
import io.nebula.market.account.application.port.output.AccountPort;
import io.nebula.market.account.domain.exception.BizException;
import io.nebula.market.account.domain.model.Account;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService implements AccountCommand, AccountQuery {
    private final AccountPort port;

    @Override
    @Transactional
    public Account create(CreateAccountRequest request) {
        Account account = Account.builder()
                .name(request.name())
                .email(request.email())
                .build();

        if (port.exists(account.getEmail())) {
            throw new BizException.AlreadyExists();
        }

        return port.save(account);
    }

    @Override
    @Transactional
    public Account update(UpdateAccountRequest request) {
        Account account = port.get(Account.builder()
                .id(request.id())
                .name(request.name())
                .build()
        ).orElseThrow(BizException.NoneExists::new);

        return account.update(Account.builder()
                .name(request.name())
                .build());
    }

    @Override
    @Transactional
    public boolean delete(DeleteAccountRequest request) {
        Account account = Account.builder()
                .id(request.id())
                .build();

        if (!port.exists(account.getId())) {
            throw new BizException.NoneExists();
        }

        port.delete(account);
        return true;
    }

    @Override
    @Cacheable(value = "account", key = "#query.id()", cacheManager = "accountCacheManager")
    @Retryable(retryFor = {RedisConnectionException.class, RedisConnectionFailureException.class}, maxAttempts = 1, backoff = @Backoff(delay = 100))
    public Account read(ReadAccountRequest query) {
        return getAccount(query);
    }

    @Recover
    @SuppressWarnings("unused")
    public Account readWithoutCache(ReadAccountRequest query) {
        return getAccount(query);
    }

    private Account getAccount(ReadAccountRequest query) {
        Account account = Account.builder()
                .id(query.id())
                .build();

        return port.get(account)
                .orElseThrow(BizException.NoneExists::new);
    }

    @Override
    @Cacheable(value = "accounts", key = "#query.pageable()", cacheManager = "accountCacheManager")
    @Retryable(retryFor = {RedisConnectionException.class, RedisConnectionFailureException.class}, maxAttempts = 1, backoff = @Backoff(delay = 100))
    public Page<Account> read(ReadAccountsRequest query) {
        return getAccounts(query);
    }

    @Recover
    @SuppressWarnings("unused")
    public Page<Account> readWithoutCache(ReadAccountsRequest query) {
        return getAccounts(query);
    }

    private Page<Account> getAccounts(ReadAccountsRequest query) {
        return port.getMultiple(query.pageable());
    }

}