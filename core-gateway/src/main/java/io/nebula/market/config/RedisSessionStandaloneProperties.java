package io.nebula.market.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

@Deprecated
@Profile("deprecated")
@Description("단일 redis 를 사용하고 싶으시다면 클러스터 설정을 해제하고 이것을 사용하세요.")
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisSessionStandaloneProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
