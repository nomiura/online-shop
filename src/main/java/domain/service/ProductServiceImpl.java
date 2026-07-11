package domain.service;


import domain.dto.request.CreateProductRequest;
import domain.dto.response.ProductResponse;
import domain.entity.Product;
import domain.repository.OrderRepository;
import domain.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

        return null;
    }

    @Override
    public void deleteProduct(Product product) {

    }

    @Override
    public ProductResponse updateProduct(Product product) {
        return null;
    }

    @Override
    public List<ProductResponse> getProducts() {
        return List.of();
    }
}
