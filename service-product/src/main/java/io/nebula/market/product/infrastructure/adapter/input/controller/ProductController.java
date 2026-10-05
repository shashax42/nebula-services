package io.nebula.market.product.infrastructure.adapter.input.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.nebula.market.product.application.dto.request.*;
import io.nebula.market.product.application.dto.response.ReadProductResponse;
import io.nebula.market.product.application.dto.response.ReadProductsResponse;
import io.nebula.market.product.application.port.input.ProductCommand;
import io.nebula.market.product.application.port.input.ProductQuery;
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
public class ProductController implements ProductApiDocs {
    private final ProductCommand command;
    private final ProductQuery query;

    @PostMapping
    public ResponseEntity<String> createProduct(@Valid @RequestBody CreateProductRequest request) {
        var product = command.create(request);

        Long id = product.getId();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        command.update(request.toBuilder().id(id).build());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        var request = DeleteProductRequest.builder().id(id).build();

        command.delete(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReadProductsResponse>> readProducts(
            @PageableDefault(sort = "id", direction = DESC) Pageable pageable
    ) {
        var request = ReadProductsRequest.builder()
                .pageable(pageable)
                .build();

        var products = query.read(request);

        var response = products.map(product -> ReadProductsResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .image(product.getImage())
                .price(product.getPrice())
                .build());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadProductResponse> readProduct(@PathVariable Long id) {
        var request = ReadProductRequest.builder()
                .id(id)
                .build();

        var product = query.read(request);

        var response = ReadProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .image(product.getImage())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .createdDate(product.getCreatedDate())
                .modifiedDate(product.getModifiedDate())
                .build();

        return ResponseEntity.ok(response);
    }
}
