package io.nebula.batch.order.service;

import io.nebula.batch.order.observability.BatchMetrics;
import java.time.Duration;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    private final BatchMetrics batchMetrics;

    public JobCompletionListener(BatchMetrics batchMetrics) {
        this.batchMetrics = batchMetrics;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("배치 작업 수행됨: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        boolean success = jobExecution.getStatus() == BatchStatus.COMPLETED;
        long rows = jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime() != null ? jobExecution.getEndTime() : LocalDateTime.now();
        Duration duration = start != null ? Duration.between(start, end) : Duration.ZERO;

        batchMetrics.recordRun(jobName, success, duration, rows);
        if (success) {
            log.info("배치 수행 완료: {} (처리 {}건, {}ms)", jobName, rows, duration.toMillis());
        } else {
            log.error("배치 수행 실패: {} (status={})", jobName, jobExecution.getStatus());
        }
        // 프로세스 종료는 BatchOrderApplication 이 한다. 여기서 System.exit 를 부르면
        // 실패해도 종료 코드가 0 이 되고(쿠버네티스는 성공으로 봄), 지표·트레이스 flush 전에 끝날 수 있다.
    }
}
