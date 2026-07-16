package domain.service;


import domain.dto.request.CreateProductRequest;
import domain.dto.request.PatchProductRequest;
import domain.dto.request.UpdateProductRequest;
import domain.dto.response.ProductResponse;


import java.util.List;

public interface ProductService {
    ProductResponse findById(Long productId);
    ProductResponse createProduct(CreateProductRequest request);
    void deleteProduct(Long productId);
    ProductResponse patchProduct(Long productId, PatchProductRequest request);
    ProductResponse fullUpdateProduct(Long productId, UpdateProductRequest request);
    List<ProductResponse> getAllProducts();

}
