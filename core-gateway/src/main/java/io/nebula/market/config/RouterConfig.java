package io.nebula.market.config;

import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
@Description("gateway의 router 설정. circuit-breaker 가 2023.0.3 내 route 부문 버그가 있음 ")
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> accountServiceRoute() {
        return RouterFunctions.route()
                .route(GatewayRequestPredicates.path("/account/**"), http())
                .filter(lb("service-account"))
                .filter(rewritePath("/account/?(?<segment>.*)", "/$\\{segment}"))
                .filter(circuitBreaker("accountCircuitBreaker", URI.create("forward:/fallback/account")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return RouterFunctions.route()
                .route(GatewayRequestPredicates.path("/order/**"), http())
                .filter(lb("service-order"))
                .filter(rewritePath("/order/?(?<segment>.*)", "/$\\{segment}"))
                .filter(circuitBreaker("orderCircuitBreaker", URI.create("forward:/fallback/order")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return RouterFunctions.route()
                .route(GatewayRequestPredicates.path("/product/**"), http())
                .filter(lb("service-product"))
                .filter(rewritePath("/product/?(?<segment>.*)", "/$\\{segment}"))
                .filter(circuitBreaker("productCircuitBreaker", URI.create("forward:/fallback/product")))
                .build();
    }

}
