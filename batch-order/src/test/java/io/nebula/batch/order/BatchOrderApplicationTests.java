package io.nebula.batch.order;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 전체 컨텍스트 기동 테스트. DB·Redis 클러스터·Kafka·Eureka가 모두 있어야 하므로
 * 기본 test 태스크에서는 제외한다 (build.gradle의 excludeTags 'integration').
 */
@Tag("integration")
@SpringBootTest
class BatchOrderApplicationTests {

    @Test
    void contextLoads() {
    }

}
