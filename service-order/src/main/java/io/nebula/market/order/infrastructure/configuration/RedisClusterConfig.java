package io.nebula.market.order.infrastructure.configuration;

import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisClusterConfig {
    private final RedisClusterProperties properties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        final List<String> nodes = properties.getNodes();
        final String password = properties.getPassword();
        final int maxRedirects = properties.getMaxRedirects();
        final List<RedisNode> redisNodes = nodes.stream()
                .map(node -> new RedisNode(node.split(":")[0], Integer.parseInt(node.split(":")[1])))
                .toList();

        // 1. Redis Cluster 설정
        var config = new RedisClusterConfiguration();
        config.setClusterNodes(redisNodes);
        config.setMaxRedirects(maxRedirects);
        config.setPassword(password);

        // 2. Socket 옵션 설정
        var socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(100L))
                .keepAlive(true)
                .build();

        // 3. Cluster Topology Refresh 옵션 설정
        var clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .dynamicRefreshSources(true)
                .enableAllAdaptiveRefreshTriggers()
                .enablePeriodicRefresh(Duration.ofMinutes(30L))
                .build();

        // 4. Lettuce Client 옵션 설정
        var clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .socketOptions(socketOptions)
                .build();

        // 5. Lettuce Client 설정
        var clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(1000L))
                .build();

        return new LettuceConnectionFactory(config, clientConfiguration);
    }

    @Bean
    public RedisOperations<String, Object> cacheRedisOperations(RedisConnectionFactory redisConnectionFactory) {
        var redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
