package io.nebula.market.order.infrastructure.adapter.output.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-product")
public interface ProductServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<?> readProduct(@PathVariable("id") Long productId);
}