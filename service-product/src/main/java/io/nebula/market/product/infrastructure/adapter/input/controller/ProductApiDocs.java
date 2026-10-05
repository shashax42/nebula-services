package io.nebula.market.product.infrastructure.adapter.input.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.nebula.market.product.application.dto.request.CreateProductRequest;
import io.nebula.market.product.application.dto.request.UpdateProductRequest;
import io.nebula.market.product.application.dto.response.ReadProductResponse;
import io.nebula.market.product.application.dto.response.ReadProductsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "상품 API", description = "상품을 생성, 조회, 수정, 삭제하는 API")
public interface ProductApiDocs {

    @Operation(summary = "새로운 상품 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상품이 성공적으로 생성됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력", content = @Content)
    })
    ResponseEntity<String> createProduct(CreateProductRequest request);

    @Operation(summary = "기존 상품 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품이 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력", content = @Content),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
    })
    ResponseEntity<Void> updateProduct(Long id, UpdateProductRequest request);

    @Operation(summary = "상품 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "상품이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
    })
    ResponseEntity<Void> deleteProduct(Long id);

    @Operation(summary = "상품 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록이 성공적으로 조회됨")
    })
    ResponseEntity<Page<ReadProductsResponse>> readProducts(Pageable pageable);

    @Operation(summary = "ID로 단일 상품 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품이 성공적으로 조회됨"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
    })
    ResponseEntity<ReadProductResponse> readProduct(Long id);
}