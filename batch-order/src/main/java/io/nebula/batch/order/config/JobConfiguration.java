package io.nebula.batch.order.config;

import io.nebula.batch.order.service.JobCompletionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfiguration {

    @Bean
    public Job cancelOrderWhereStateIsPendingJob(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            StepConfiguration stepConfiguration,
            JobCompletionListener listener
    ) {
        return new JobBuilder("cancelOrderWhereStateIsPendingJob", jobRepository)
                .start(stepConfiguration.changeOrderStateStep(jobRepository, transactionManager))
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .build();
    }

    @Bean
    public Job deleteOrderWhereStateIsCanceledJob(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            StepConfiguration stepConfiguration,
            JobCompletionListener listener
    ) {
        return new JobBuilder("deleteOrderWhereStateIsCanceledJob", jobRepository)
                .start(stepConfiguration.deleteOrderStep(jobRepository, transactionManager))
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .build();
    }

}