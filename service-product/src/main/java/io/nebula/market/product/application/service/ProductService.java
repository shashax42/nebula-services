package io.nebula.market.product.application.service;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import io.nebula.market.product.application.dto.request.*;
import io.nebula.market.product.application.port.input.ProductCommand;
import io.nebula.market.product.application.port.input.ProductQuery;
import io.nebula.market.product.application.port.output.ProductPort;
import io.nebula.market.product.domain.event.Events;
import io.nebula.market.product.domain.event.OrderCanceledEvent;
import io.nebula.market.product.domain.exception.BizException;
import io.nebula.market.product.domain.model.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements ProductCommand, ProductQuery {
    private final ProductPort port;

    @Override
    @Transactional
    public Product create(CreateProductRequest request) {
        return port.save(Product.builder()
                .name(request.name())
                .image(request.image())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build());
    }

    @Override
    @Transactional
    public Product update(UpdateProductRequest request) {
        Product product = port.get(request.id())
                .orElseThrow(BizException.NoneExists::new);

        return product.update(Product.builder()
                .name(request.name())
                .image(request.image())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build());
    }

    @Override
    @Transactional
    public boolean delete(DeleteProductRequest request) {
        if (!port.exists(request.id())) {
            throw new BizException.NoneExists();
        }

        Product product = Product.builder()
                .id(request.id())
                .build();

        port.delete(product);
        return true;
    }

    @Override
    @Cacheable(value = "product", key = "#query.id()", cacheManager = "productCacheManager")
    @Retryable(retryFor = {RedisConnectionException.class}, maxAttempts = 1, backoff = @Backoff(delay = 100))
    public Product read(ReadProductRequest query) {
        return getProduct(query);
    }

    @Recover
    @SuppressWarnings("unused")
    public Product readWithoutCache(ReadProductRequest query) {
        return getProduct(query);
    }
    private Product getProduct(ReadProductRequest query) {
        return port.get(query.id())
                .orElseThrow(BizException.NoneExists::new);
    }

    @Override
    @Cacheable(value = "products", key = "#query.pageable()", cacheManager = "productCacheManager")
    @Retryable(retryFor = {RedisConnectionException.class}, maxAttempts = 1, backoff = @Backoff(delay = 100))
    public Page<Product> read(ReadProductsRequest query) {
        return getProducts(query);
    }

    @Recover
    @SuppressWarnings("unused")
    public Page<Product> readWithoutCache(ReadProductsRequest query) {
        return getProducts(query);
    }

    private Page<Product> getProducts(ReadProductsRequest query) {
        return port.getMultiple(query.pageable());
    }

    @Override
    @Transactional
    public Product updateStock(UpdateProductStockDecreaseRequest request) {
        Product product = port.get(request.productId())
                .orElseThrow(BizException.NoneExists::new);

        if (product.getStock() < request.quantity()) {
            Events.raise(OrderCanceledEvent.builder()
                    .orderId(request.orderId())
                    .accountId(request.accountId())
                    .productId(request.productId())
                    .price(product.getPrice())
                    .quantity(request.quantity())
                    .reason("재고 부족>" + product.getStock())
                    .build());
            return product;
        }

        return product.decreaseStock(request.quantity());
    }


}
