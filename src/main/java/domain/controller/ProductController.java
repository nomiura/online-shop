package domain.controller;


import domain.dto.request.PatchProductRequest;
import domain.dto.request.UpdateProductRequest;
import domain.dto.response.ProductResponse;
import domain.entity.Product;
import domain.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;


    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.findById(productId);

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


}
