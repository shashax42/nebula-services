package io.nebula.market.order.infrastructure.adapter.output.feign;

import lombok.RequiredArgsConstructor;
import io.nebula.market.order.application.service.ProductService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFeignAdapter implements ProductService {
    private final ProductServiceClient productServiceClient;

    @Override
    public boolean checkProductExists(Long productId) {
        var response = productServiceClient.readProduct(productId);
        return response.getStatusCode().is2xxSuccessful();
    }
}
