package io.nebula.market.account.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import io.nebula.market.account.application.port.output.AccountPort;
import io.nebula.market.account.domain.model.Account;
import io.nebula.market.account.domain.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountPort {
    private final AccountRepository repository;

    @Override
    public Account save(Account account) {
        return repository.save(account);
    }

    @Override
    public void delete(Account account) {
        repository.delete(account);
    }

    @Override
    public Optional<Account> get(Account account) {
        return repository.findById(account.getId());
    }

    @Override
    public Page<Account> getMultiple(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean exists(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean exists(UUID id) {
        return repository.existsById(id);
    }
}
