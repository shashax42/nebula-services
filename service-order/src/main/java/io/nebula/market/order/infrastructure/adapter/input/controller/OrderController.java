package io.nebula.market.order.infrastructure.adapter.input.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.nebula.market.order.application.dto.request.CreateOrderRequest;
import io.nebula.market.order.application.dto.request.DeleteOrderRequest;
import io.nebula.market.order.application.dto.request.ReadOrderRequest;
import io.nebula.market.order.application.dto.request.ReadOrdersRequest;
import io.nebula.market.order.application.dto.response.ReadOrderResponse;
import io.nebula.market.order.application.dto.response.ReadOrdersResponse;
import io.nebula.market.order.application.port.input.OrderCommand;
import io.nebula.market.order.application.port.input.OrderQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

@CrossOrigin
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OrderController implements OrderApiDocs {
    private final OrderCommand command;
    private final OrderQuery query;

    @PostMapping
    public ResponseEntity<String> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var order = command.create(request);

        Long id = order.getId();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        var request = DeleteOrderRequest.builder()
                .id(id)
                .build();

        command.delete(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReadOrdersResponse>> readOrders(
            @PageableDefault(sort = "id", direction = DESC) Pageable pageable
    ) {
        var request = ReadOrdersRequest.builder()
                .pageable(pageable)
                .build();

        var orders = query.read(request);

        var response = orders.map(order -> ReadOrdersResponse.builder()
                .id(order.getId())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .orderDate(order.getOrderDate())
                .build());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadOrderResponse> readOrder(@PathVariable Long id) {
        var request = ReadOrderRequest.builder()
                .id(id)
                .build();

        var order = query.read(request);

        var response = ReadOrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .accountId(order.getAccountId())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .orderDate(order.getOrderDate())
                .build();

        return ResponseEntity.ok(response);
    }
}
