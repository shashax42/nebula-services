package io.nebula.batch.order.config;

import io.nebula.batch.order.constant.OrderState;
import io.nebula.batch.order.service.OrderDirectDBAdapter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepConfiguration {

    @Autowired
    private OrderDirectDBAdapter adapter;

    @Bean
    public Step changeOrderStateStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("changeOrderStateStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int rows = adapter.updateOrderState(OrderState.CANCELED, OrderState.PENDING, 1);
                    contribution.incrementWriteCount(rows);
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step deleteOrderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteOrderStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int rows = adapter.deleteOrder(OrderState.CANCELED, 1);
                    contribution.incrementWriteCount(rows);
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
}
