package io.nebula.market.order.infrastructure.adapter.input.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.nebula.market.order.application.dto.request.CreateOrderRequest;
import io.nebula.market.order.application.dto.response.ReadOrderResponse;
import io.nebula.market.order.application.dto.response.ReadOrdersResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "주문 API", description = "주문, 주문 조회(단건/다건), 주문 삭제로 구성된 API")
public interface OrderApiDocs {

    @Operation(summary = "새 주문 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주문이 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력", content = @Content)
    })
    ResponseEntity<String> createOrder(CreateOrderRequest request);

    @Operation(summary = "ID로 주문 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "주문이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content)
    })
    ResponseEntity<Void> deleteOrder(Long id);

    @Operation(summary = "주문 목록 페이징 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문이 성공적으로 조회됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력", content = @Content)
    })
    ResponseEntity<Page<ReadOrdersResponse>> readOrders(Pageable pageable);

    @Operation(summary = "ID로 주문 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문이 성공적으로 조회됨"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content)
    })
    ResponseEntity<ReadOrderResponse> readOrder(Long id);
}