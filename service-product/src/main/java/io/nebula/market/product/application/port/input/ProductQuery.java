package io.nebula.market.product.application.port.input;

import io.nebula.market.product.application.dto.request.ReadProductRequest;
import io.nebula.market.product.application.dto.request.ReadProductsRequest;
import io.nebula.market.product.domain.model.Product;
import org.springframework.data.domain.Page;

public interface ProductQuery {
    Product read(ReadProductRequest query);

    Page<Product> read(ReadProductsRequest query);
}
