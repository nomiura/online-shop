package domain.service;


import domain.dto.request.CreateProductRequest;
import domain.dto.request.DeleteProductRequest;
import domain.dto.request.UpdateProductRequest;
import domain.dto.response.ProductResponse;


import java.util.List;

public interface ProductService {
    ProductResponse findById(Long productId);
    ProductResponse createProduct(CreateProductRequest request);
    void deleteProduct(DeleteProductRequest request);
    ProductResponse updateProduct(UpdateProductRequest request);
    List<ProductResponse> getAllProducts();

}
