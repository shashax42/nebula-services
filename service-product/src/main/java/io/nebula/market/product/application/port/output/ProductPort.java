package io.nebula.market.product.application.port.output;

import io.nebula.market.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductPort {
    Product save(Product product);

    void delete(Product product);

    Optional<Product> get(Long id);

    Page<Product> getMultiple(Pageable pageable);

    boolean exists(Long id);

}