package domain.service;


import domain.dto.request.CreateProductRequest;
import domain.dto.response.ProductResponse;
import domain.entity.Product;


import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    void deleteProduct(Product product);
    ProductResponse updateProduct(Product product);
    List<ProductResponse> getProducts();

}
