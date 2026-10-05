package io.nebula.market.account.infrastructure.adapter.input.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.nebula.market.account.application.dto.request.*;
import io.nebula.market.account.application.dto.response.ReadAccountResponse;
import io.nebula.market.account.application.dto.response.ReadAccountsResponse;
import io.nebula.market.account.application.port.input.AccountCommand;
import io.nebula.market.account.application.port.input.AccountQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@CrossOrigin
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AccountController implements AccountApiDocs {
    private final AccountCommand command;
    private final AccountQuery query;

    @Override
    @PostMapping
    public ResponseEntity<String> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        var account = command.create(request);

        UUID id = account.getId();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @PutMapping("{id}")
    public ResponseEntity<Void> updateAccount(@PathVariable UUID id, @Valid @RequestBody UpdateAccountRequest request) {
        command.update(request.toBuilder()
                .id(id)
                .build());

        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        var request = DeleteAccountRequest.builder()
                .id(id)
                .build();

        command.delete(request);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<ReadAccountsResponse>> getAccounts(
            @PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        ReadAccountsRequest request = ReadAccountsRequest.builder()
                .pageable(pageable)
                .build();

        var accounts = query.read(request);

        var response = accounts.map(account -> ReadAccountsResponse.builder()
                .email(account.getEmail())
                .name(account.getName())
                .createdDate(account.getCreatedDate())
                .modifiedDate(account.getModifiedDate())
                .build());

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @GetMapping("{id}")
    public ResponseEntity<ReadAccountResponse> getAccountById(@PathVariable UUID id) {
        var request = ReadAccountRequest.builder()
                .id(id)
                .build();

        var account = query.read(request);

        var response = ReadAccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .name(account.getName())
                .createdDate(account.getCreatedDate())
                .modifiedDate(account.getModifiedDate())
                .build();

        return ResponseEntity.ok(response);
    }
}
