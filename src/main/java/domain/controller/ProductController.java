package domain.controller;


import domain.dto.request.CreateProductRequest;
import domain.dto.request.PatchProductRequest;
import domain.dto.request.UpdateProductRequest;
import domain.dto.response.ProductResponse;
import domain.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;


    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.findById(productId);

    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts () {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> fullUpdateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.fullUpdateProduct(productId, request));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> patchProduct(
            @PathVariable Long productId,
            @Valid @RequestBody PatchProductRequest request) {
        return ResponseEntity.ok(productService.patchProduct(productId, request));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
