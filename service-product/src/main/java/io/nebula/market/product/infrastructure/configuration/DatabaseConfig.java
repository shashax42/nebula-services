package io.nebula.market.product.infrastructure.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari.source")
    public DataSourceProperties sourceDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.source.configuration")
    public DataSource sourceDataSource() {
        return sourceDatasourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari.replica")
    public DataSourceProperties replicaDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.replica.configuration")
    public DataSource replicaDataSource() {
        return replicaDatasourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @DependsOn({"sourceDataSource", "replicaDataSource"})
    @Bean
    public DataSource routingDataSource(
            @Qualifier("sourceDataSource") DataSource source, @Qualifier("replicaDataSource") DataSource replica) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = Map.ofEntries(
                Map.entry("source", source),
                Map.entry("replica", replica)
        );

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(source);

        return routingDataSource;
    }

    @DependsOn({"routingDataSource"})
    @Primary
    @Bean(name = "customDataSource")
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}