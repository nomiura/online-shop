package domain.service;


import domain.dto.request.*;
import domain.dto.response.ProductResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProductService {
    ProductResponse findById(Long productId);
    ProductResponse createProduct(CreateProductRequest request);
    void deleteProduct(Long productId);
    ProductResponse updateProduct(Long productId, UpdateProductRequest request);
    ProductResponse fullUpdateProduct(Long productId, FullUpdateProductRequest request);

    @Transactional(readOnly = true)
    ProductResponse getProduct(Long productId);

    List<ProductResponse> getAllProducts();


}
