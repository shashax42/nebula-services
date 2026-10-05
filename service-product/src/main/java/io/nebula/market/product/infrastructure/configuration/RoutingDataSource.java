package io.nebula.market.product.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
    private static final String SOURCE = "source";
    private static final String REPLICA = "replica";

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? REPLICA : SOURCE;

        log.debug("Current DataSource is {}", dataSourceName);

        return dataSourceName;
    }
}
