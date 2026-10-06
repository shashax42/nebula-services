package io.nebula.market.product.domain.repository;

import io.nebula.market.product.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 MySQL(Testcontainers)에 대한 JPA 매핑 테스트.
 * Docker가 없는 환경에서는 건너뛴다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class ProductRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

    @Configuration
    @EntityScan(basePackageClasses = Product.class)
    @EnableJpaRepositories(basePackageClasses = ProductRepository.class)
    static class JpaSliceConfig {
    }

    @Autowired
    ProductRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    @DisplayName("저장하면 TSID가 발급되고 생성·수정 시각이 DB 기본값으로 채워진다")
    void savesWithTsidAndTimestamps() {
        Product saved = repository.save(Product.builder()
                .name("키보드").image("https://img/1.png").description("기계식")
                .price(30_000L).stock(5)
                .build());
        em.flush();
        em.clear();

        Product found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isPositive();
        assertThat(found.getName()).isEqualTo("키보드");
        assertThat(found.getCreatedDate()).isNotNull();
        assertThat(found.getModifiedDate()).isNotNull();
    }

    @Test
    @DisplayName("재고 차감이 dirty checking으로 DB에 반영된다")
    void persistsStockDecrease() {
        Product saved = repository.save(Product.builder()
                .name("마우스").image("https://img/2.png").description("무선")
                .price(20_000L).stock(5)
                .build());
        em.flush();
        em.clear();

        repository.findById(saved.getId()).orElseThrow().decreaseStock(2);
        em.flush();
        em.clear();

        assertThat(repository.findById(saved.getId()).orElseThrow().getStock()).isEqualTo(3);
    }
}
