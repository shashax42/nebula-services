package io.nebula.market.account.application.service;

import io.nebula.market.account.application.dto.request.CreateAccountRequest;
import io.nebula.market.account.application.dto.request.DeleteAccountRequest;
import io.nebula.market.account.application.dto.request.UpdateAccountRequest;
import io.nebula.market.account.application.port.output.AccountPort;
import io.nebula.market.account.domain.exception.BizException;
import io.nebula.market.account.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * AccountService 단위 테스트. AccountPort만 목으로 대체한다.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountPort port;

    @InjectMocks
    AccountService service;

    @Nested
    @DisplayName("가입")
    class Create {

        @Test
        @DisplayName("새 이메일이면 저장한다")
        void savesNewAccount() {
            given(port.exists("a@example.com")).willReturn(false);
            given(port.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

            Account account = service.create(new CreateAccountRequest("a@example.com", "에이"));

            assertThat(account.getEmail()).isEqualTo("a@example.com");
            assertThat(account.getName()).isEqualTo("에이");
        }

        @Test
        @DisplayName("이미 있는 이메일이면 저장하지 않고 AlreadyExists 예외를 던진다")
        void rejectsDuplicateEmail() {
            given(port.exists("a@example.com")).willReturn(true);

            assertThatThrownBy(() -> service.create(new CreateAccountRequest("a@example.com", "에이")))
                    .isInstanceOf(BizException.AlreadyExists.class)
                    .hasMessage("이미 존재하는 계정");
            then(port).should().exists("a@example.com");
            then(port).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("수정")
    class Update {

        @Test
        @DisplayName("이름만 바꾸고 이메일은 유지한다")
        void changesNameOnly() {
            UUID id = UUID.randomUUID();
            Account existing = Account.builder().id(id).email("a@example.com").name("에이").build();
            given(port.get(any(Account.class))).willReturn(Optional.of(existing));

            Account updated = service.update(new UpdateAccountRequest(id, "비"));

            assertThat(updated.getName()).isEqualTo("비");
            assertThat(updated.getEmail()).isEqualTo("a@example.com");
            ArgumentCaptor<Account> lookup = ArgumentCaptor.forClass(Account.class);
            then(port).should().get(lookup.capture());
            assertThat(lookup.getValue().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("없는 계정이면 NoneExists 예외를 던진다")
        void rejectsUnknownAccount() {
            given(port.get(any(Account.class))).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(new UpdateAccountRequest(UUID.randomUUID(), "비")))
                    .isInstanceOf(BizException.NoneExists.class)
                    .hasMessage("존재하지 않는 계정");
        }
    }

    @Test
    @DisplayName("없는 계정은 삭제하지 않는다")
    void rejectsDeletingUnknownAccount() {
        UUID id = UUID.randomUUID();
        given(port.exists(id)).willReturn(false);

        assertThatThrownBy(() -> service.delete(new DeleteAccountRequest(id)))
                .isInstanceOf(BizException.NoneExists.class);
        then(port).should().exists(id);
        then(port).shouldHaveNoMoreInteractions();
    }
}
