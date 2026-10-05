package io.nebula.market.product.application.port.input;

import io.nebula.market.product.application.dto.request.CreateProductRequest;
import io.nebula.market.product.application.dto.request.DeleteProductRequest;
import io.nebula.market.product.application.dto.request.UpdateProductRequest;
import io.nebula.market.product.application.dto.request.UpdateProductStockDecreaseRequest;
import io.nebula.market.product.domain.model.Product;

public interface ProductCommand {
    Product create(CreateProductRequest request);

    Product update(UpdateProductRequest request);

    boolean delete(DeleteProductRequest request);

    Product updateStock(UpdateProductStockDecreaseRequest request);
}
