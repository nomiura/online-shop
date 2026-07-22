package domain.service;


import domain.dto.request.*;
import domain.dto.response.ProductResponse;
import domain.entity.Order;


import java.util.List;

public interface ProductService {
    ProductResponse findById(Long productId);
    ProductResponse createProduct(CreateProductRequest request);
    void deleteProduct(Long productId);
    ProductResponse patchProduct(Long productId, PatchProductRequest request);
    ProductResponse fullUpdateProduct(Long productId, UpdateProductRequest request);
    List<ProductResponse> getAllProducts();

}
