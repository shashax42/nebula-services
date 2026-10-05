package io.nebula.batch.order.service;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("배치 작업 수행됨: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("배치 수행 완료: " + jobExecution.getJobInstance().getJobName());
        } else {
            System.out.println("배치 수행 실패: " + jobExecution.getJobInstance().getJobName());
        }

        System.exit(0);
    }
}