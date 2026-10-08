package io.nebula.batch.order.observability;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class BatchMetricsTest {

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private final BatchMetrics metrics = new BatchMetrics(registry);

    @Test
    void recordsLastRunDurationAndRowsOnSuccess() {
        long before = Instant.now().getEpochSecond();

        metrics.recordRun("cancelJob", true, Duration.ofMillis(1500), 7);

        assertThat(registry.get("nebula.batch.job.last.run").tags("job.name", "cancelJob", "status", "completed")
                .gauge().value()).isGreaterThanOrEqualTo(before);
        assertThat(registry.get("nebula.batch.job.duration").tags("status", "completed").gauge().value()).isEqualTo(1.5);
        assertThat(registry.get("nebula.batch.order.rows").tags("job.name", "cancelJob").gauge().value()).isEqualTo(7);
    }

    @Test
    void failedRunDoesNotOverwriteRowsAndUsesFailedStatus() {
        metrics.recordRun("deleteJob", true, Duration.ofSeconds(1), 3);
        metrics.recordRun("deleteJob", false, Duration.ofSeconds(2), 0);

        assertThat(registry.get("nebula.batch.job.last.run").tags("job.name", "deleteJob", "status", "failed")
                .gauge().value()).isPositive();
        assertThat(registry.get("nebula.batch.order.rows").tags("job.name", "deleteJob").gauge().value()).isEqualTo(3);
    }

    @Test
    void repeatedRunsUpdateTheSameSeries() {
        metrics.recordRun("cancelJob", true, Duration.ofSeconds(1), 2);
        metrics.recordRun("cancelJob", true, Duration.ofSeconds(1), 5);

        assertThat(registry.find("nebula.batch.order.rows").gauges()).hasSize(1);
        assertThat(registry.get("nebula.batch.order.rows").gauge().value()).isEqualTo(5);
    }
}
