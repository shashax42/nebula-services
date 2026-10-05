package io.nebula.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.nebula.market.repository.Account;
import io.nebula.market.repository.AccountRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final AccountRepository repository;

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(oidcUserRequest);

        registerOrUpdate(oidcUser.getUserInfo());

        Account account = repository.findAccountByEmail(oidcUser.getUserInfo().getEmail());

        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUserRequest.getIdToken(),
                updateUserInfo(oidcUser.getUserInfo(), account));
    }

    private OidcUserInfo updateUserInfo(OidcUserInfo info, Account account) {
        Map<String, Object> claims = new HashMap<>(info.getClaims());
        claims.put("email", account.getEmail());
        claims.put("nickname", account.getName());

        return new OidcUserInfo(claims);
    }

    private void registerOrUpdate(OidcUserInfo oidcUserInfo) {
        String email = oidcUserInfo.getEmail();
        String name = oidcUserInfo.getNickName();

        Account account = repository.findAccountByEmail(email);

        if (account == null) {
            account = Account.builder()
                    .email(email)
                    .name(name)
                    .build();
        } else {
            account.update(Account.builder()
                    .name(name)
                    .build()
            );
        }

        repository.save(account);
    }
}
