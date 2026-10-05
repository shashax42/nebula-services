package io.nebula.market.product.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import io.nebula.market.product.application.port.output.ProductPort;
import io.nebula.market.product.domain.model.Product;
import io.nebula.market.product.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPort {
    private final ProductRepository repository;

    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

    @Override
    public void delete(Product product) {
        repository.delete(product);
    }

    @Override
    public Optional<Product> get(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Product> getMultiple(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean exists(Long id) {
        return repository.existsById(id);
    }
}
