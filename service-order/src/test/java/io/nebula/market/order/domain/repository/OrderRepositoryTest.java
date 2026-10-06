package io.nebula.market.order.domain.repository;

import io.nebula.market.order.domain.model.Order;
import io.nebula.market.order.domain.model.OrderState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 MySQL(Testcontainers)에 대한 JPA 매핑 테스트.
 * 예약어 테이블명(`order`), TSID 생성, DB 기본값처럼 H2로는 검증할 수 없는 부분을 확인한다.
 * Docker가 없는 환경(예: Docker 없는 로컬 PC)에서는 건너뛴다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class OrderRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

    /**
     * 메인 애플리케이션 클래스(@EnableFeignClients, @EnableDiscoveryClient) 대신
     * JPA에 필요한 것만 올리는 최소 설정.
     */
    @Configuration
    @EntityScan(basePackageClasses = Order.class)
    @EnableJpaRepositories(basePackageClasses = OrderRepository.class)
    static class JpaSliceConfig {
    }

    @Autowired
    OrderRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("저장하면 TSID가 발급되고, 비워 둔 컬럼은 DB 기본값으로 채워진다")
    void savesWithTsidAndDbDefaults() {
        Order saved = repository.save(Order.builder()
                .productId(10L)
                .accountId(UUID.randomUUID())
                .price(5_000L)
                .build());
        em.flush();
        em.clear();

        Order found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isPositive();
        assertThat(found.getOrderState()).isEqualTo(OrderState.PENDING);
        assertThat(found.getQuantity()).isEqualTo(1);
        assertThat(found.getOrderDate()).isNotNull();
    }

    @Test
    @DisplayName("페이지 단위로 조회한다")
    void readsPage() {
        for (int i = 0; i < 3; i++) {
            repository.save(Order.builder()
                    .productId(10L).accountId(UUID.randomUUID()).price(1L).quantity(1)
                    .orderState(OrderState.PENDING)
                    .build());
        }
        em.flush();

        var page = repository.findAll(PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }
}
