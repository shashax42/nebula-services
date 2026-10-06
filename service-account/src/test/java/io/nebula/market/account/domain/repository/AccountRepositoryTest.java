package io.nebula.market.account.domain.repository;

import io.nebula.market.account.domain.model.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 실제 MySQL(Testcontainers)에 대한 JPA 매핑 테스트.
 * Docker가 없는 환경에서는 건너뛴다.
 * 시드 데이터(data.sql, a@example.com 등)는 끄고 테스트가 넣은 데이터만으로 검증한다.
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class AccountRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

    /**
     * Account는 @DynamicInsert가 없어 created_date/modified_date를 JPA Auditing이 채운다.
     * 운영의 JpaConfig와 같은 방식(OffsetDateTime 제공자)으로 Auditing을 켠다.
     */
    @Configuration
    @EntityScan(basePackageClasses = Account.class)
    @EnableJpaRepositories(basePackageClasses = AccountRepository.class)
    @EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
    static class JpaSliceConfig {

        @Bean
        DateTimeProvider auditingDateTimeProvider() {
            return () -> Optional.of(OffsetDateTime.now());
        }
    }

    @Autowired
    AccountRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("이메일로 존재 여부를 조회한다")
    void existsByEmail() {
        repository.save(Account.builder().email("repo-test@nebula.test").name("에이").build());
        em.flush();

        assertThat(repository.existsByEmail("repo-test@nebula.test")).isTrue();
        assertThat(repository.existsByEmail("nobody@nebula.test")).isFalse();
    }

    @Test
    @DisplayName("같은 이메일은 DB 유니크 제약으로 막힌다")
    void emailIsUnique() {
        repository.save(Account.builder().email("repo-test@nebula.test").name("에이").build());
        em.flush();

        assertThatThrownBy(() -> {
            repository.save(Account.builder().email("repo-test@nebula.test").name("비").build());
            em.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
