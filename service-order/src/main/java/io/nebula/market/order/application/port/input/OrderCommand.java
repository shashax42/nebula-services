package io.nebula.market.order.application.port.input;

import io.nebula.market.order.application.dto.request.CreateOrderRequest;
import io.nebula.market.order.application.dto.request.DeleteOrderRequest;
import io.nebula.market.order.application.dto.request.UpdateOrderStateRequest;
import io.nebula.market.order.domain.model.Order;

public interface OrderCommand {
    Order create(CreateOrderRequest request);

    boolean delete(DeleteOrderRequest request);

    Order updateOrderState(UpdateOrderStateRequest request);
}
