package io.nebula.batch.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.nebula.batch.order.observability.BatchMetrics;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

class JobCompletionListenerTest {

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private final JobCompletionListener listener = new JobCompletionListener(new BatchMetrics(registry));

    private JobExecution execution(BatchStatus status, long... writeCounts) {
        JobExecution execution = new JobExecution(new JobInstance(1L, "cancelOrderWhereStateIsPendingJob"), 1L, new JobParameters());
        execution.setStartTime(LocalDateTime.now().minusSeconds(2));
        execution.setEndTime(LocalDateTime.now());
        execution.setStatus(status);
        for (int i = 0; i < writeCounts.length; i++) {
            StepExecution step = new StepExecution("step" + i, execution);
            step.setWriteCount(writeCounts[i]);
            execution.addStepExecutions(java.util.List.of(step));
        }
        return execution;
    }

    @Test
    void sumsStepWriteCountsIntoRowsGauge() {
        listener.afterJob(execution(BatchStatus.COMPLETED, 4, 6));

        assertThat(registry.get("nebula.batch.order.rows").tags("job.name", "cancelOrderWhereStateIsPendingJob")
                .gauge().value()).isEqualTo(10);
        assertThat(registry.get("nebula.batch.job.duration").tags("status", "completed").gauge().value()).isGreaterThan(1.0);
    }

    @Test
    void failedJobIsRecordedAsFailedAndDoesNotExitTheJvm() {
        listener.afterJob(execution(BatchStatus.FAILED));

        assertThat(registry.get("nebula.batch.job.last.run").tags("status", "failed").gauge().value()).isPositive();
        assertThat(registry.find("nebula.batch.order.rows").gauge()).isNull();
    }
}
