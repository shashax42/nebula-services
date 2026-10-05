package io.nebula.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class CoreDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreDiscoveryApplication.class, args);
    }

}
