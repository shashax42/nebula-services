package io.nebula.market.order.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import io.nebula.market.order.application.port.output.OrderPort;
import io.nebula.market.order.domain.model.Order;
import io.nebula.market.order.domain.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPort {
    private final OrderRepository repository;

    @Override
    public Order save(Order order) {
        return repository.save(order);
    }

    @Override
    public void delete(Order order) {
        repository.delete(order);
    }

    @Override
    public Optional<Order> get(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Order> getMultiple(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean exists(Long id) {
        return repository.existsById(id);
    }
}
