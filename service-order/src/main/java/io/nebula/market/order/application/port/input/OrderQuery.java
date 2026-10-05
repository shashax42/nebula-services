package io.nebula.market.order.application.port.input;

import io.nebula.market.order.application.dto.request.ReadOrderRequest;
import io.nebula.market.order.application.dto.request.ReadOrdersRequest;
import io.nebula.market.order.domain.model.Order;
import org.springframework.data.domain.Page;

public interface OrderQuery {
    Order read(ReadOrderRequest query);

    Page<Order> read(ReadOrdersRequest query);
}
